from api import VERSION
from django.http import JsonResponse
import django
from django.contrib.auth.models import User
from rest_framework import status
from rest_framework.views import APIView
from rest_framework.parsers import JSONParser
from django.core.exceptions import ObjectDoesNotExist
from rest_framework import generics
from drf_yasg import openapi
from drf_yasg.inspectors import SwaggerAutoSchema
from drf_yasg.utils import swagger_auto_schema
from django.http import HttpResponse, JsonResponse
from api.decorators import login_required_view
from api.serializers import VersionSerializer, WorkHoursSerializer, UserProfileSerializer, CodeSerializer, MessageSerializer
from api.utils import Message
from django.http.response import HttpResponseBadRequest
from rest_framework.permissions import IsAuthenticated
from rest_framework.renderers import JSONRenderer
from django.conf import settings
from qr_code.gr_code import generate_qr_code, validate_code
from api.models import WorkDay
from django.utils import timezone
import io

class VersionView(APIView):
  
    @swagger_auto_schema(
        operation_id='version',
        operation_description='Return current API version',
        responses={200: VersionSerializer}
    )
    def get(self, request):
        serializer = VersionSerializer({'version': VERSION})
        return JsonResponse(serializer.data)

class CodeView(APIView):
    permission_classes = (IsAuthenticated,)
  
    @swagger_auto_schema(
        operation_id='get_qr_code',
        operation_description='Get QR code',
    )
    def get(self, request):
        qr_image = generate_qr_code(settings.OTP_SECRET)
        byteIO = io.BytesIO()
        qr_image.save(byteIO, format='PNG')
        byteArr = byteIO.getvalue()
        return HttpResponse(byteArr, content_type="image/png")

    @swagger_auto_schema(
    operation_id='send_code',
    operation_description='Send scanned code to start workday',
    request_body=CodeSerializer,
    response=MessageSerializer
    )
    def post(self, request):
        data = JSONParser().parse(request)
        serializer = CodeSerializer(data=data)
        if serializer.is_valid():
            code = data['code']
            code_valid = validate_code(settings.OTP_SECRET, code)
            if (code_valid):
                new_work_day, work_day = self.start_new_work_day_or_finish_last_one(request.user)
                serializer = None
                if new_work_day:
                    serializer = MessageSerializer(Message('New work day started'))
                else:
                    serializer = MessageSerializer(Message('Work day finished'))
                return JsonResponse(serializer.data, status=200)
            else:
                serializer = MessageSerializer(Message('Invalid code'))
                return HttpResponseBadRequest(JSONRenderer().render(serializer.data))
        else:
            return JsonResponse(serializer.errors, safe=False, status=400)

    def start_new_work_day_or_finish_last_one(self, user):
        latest_work_day = None
        try:
            latest_work_day = WorkDay.objects.filter(user=user).latest('started')
        except ObjectDoesNotExist:
            pass
        if latest_work_day != None and latest_work_day.finished == None:
            latest_work_day.finished = timezone.now()
            latest_work_day.save()
            return False, latest_work_day
        else:
            # start new workday
            new_work_day = WorkDay.objects.create(user=user)
            new_work_day.save()
            return True, new_work_day

class EmployeesView(APIView):
    permission_classes = (IsAuthenticated,)

    def has_permission(self, user):
        return user.groups.filter(name='Employer').exists() or user.is_superuser
  
    @swagger_auto_schema(
        operation_id='employee',
        operation_description='Return all employees',
        responses={200: UserProfileSerializer(many=True)}
    )
    def get(self, request):
        if self.has_permission(request.user):
            employees = User.objects.filter(groups__name='Employee')
            serializer = UserProfileSerializer(employees, many=True)
            return JsonResponse(serializer.data, safe=False, status=200)
        else:
            serializer = MessageSerializer(Message('Only employers can access this endpoint'))
            return JsonResponse(serializer.data, safe=False, status=403)
        
class WorkHoursView(APIView):

    def check_if_user_is_employee(self, user):
        return user.groups.filter(name='Employee').exists()

    def has_permission(self, user):
        return user.groups.filter(name='Employer').exists() or user.is_superuser
  
    @swagger_auto_schema(
        operation_id='workhours',
        operation_description='Return employee workhours',
        responses={200: WorkHoursSerializer(many=True)}
    )
    def get(self, request, employee_id):
        if not self.has_permission(request.user):
            serializer = MessageSerializer(Message('Only employers can access this endpoint'))
            return JsonResponse(serializer.data, safe=False, status=403)
           
        user = None
        try:
            user = User.objects.get(pk=employee_id)
            if self.check_if_user_is_employee(user):
                work_days = WorkDay.objects.filter(user=user)
                serializer = WorkHoursSerializer(work_days, many=True)
                return JsonResponse(serializer.data, safe=False, status=200)
            else:
                serializer = MessageSerializer(Message('Given user must be employee'))
                return HttpResponseBadRequest(JSONRenderer().render(serializer.data))
        except ObjectDoesNotExist:
            serializer = MessageSerializer(Message('No user with given id exists'))
            return JsonResponse(serializer.data, safe=False, status=404)
