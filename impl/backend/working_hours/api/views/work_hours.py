from django.http import JsonResponse
from django.contrib.auth.models import User
from rest_framework.views import APIView
from django.core.exceptions import ObjectDoesNotExist
from drf_yasg.utils import swagger_auto_schema
from django.http import JsonResponse
from api.serializers import WorkHoursSerializer, MessageSerializer
from api.utils import Message
from django.http.response import HttpResponseBadRequest
from rest_framework.permissions import IsAuthenticated
from rest_framework.renderers import JSONRenderer
from rest_framework.pagination import LimitOffsetPagination
from django.conf import settings
from api.models import WorkDay
from datetime import datetime
import api.scheme as scheme
   
class WorkHoursView(APIView):
    permission_classes = (IsAuthenticated,)

    def check_if_user_is_employee(self, user):
        return user.groups.filter(name='Employee').exists()

    def has_permission(self, user):
        return user.groups.filter(name='Employer').exists() or user.is_superuser

    def make_paginated_response(self, queryset):
        paginator = LimitOffsetPagination()
        queryset = paginator.paginate_queryset(queryset, self.request)
        serializer = WorkHoursSerializer(queryset, many=True)
        return paginator.get_paginated_response(serializer.data)

    def filter_data(self, queryset):
        date = self.request.query_params.get('date', None)
        if date is not None:
            date = datetime.fromtimestamp(int(date) / 1000)
            # filter by date
            queryset = queryset.filter(started__year=date.year, 
                         started__month=date.month, 
                         started__day=date.day) | queryset.filter(finished__year=date.year, 
                         finished__month=date.month, 
                         finished__day=date.day)
        return queryset

    def get_employee_workhours(self, employee):
        if self.check_if_user_is_employee(employee):
            work_days = WorkDay.objects.filter(user=employee)
            try:
                work_days = self.filter_data(work_days)
                work_days.order_by('+finished')
                return self.make_paginated_response(work_days)
            except Exception as e:
                serializer = MessageSerializer(Message('Invalid filters.'))
                return JsonResponse(serializer.data, safe=False, status=400)
        else:
            serializer = MessageSerializer(Message('Given user must be employee'))
            return HttpResponseBadRequest(JSONRenderer().render(serializer.data))
  
    @swagger_auto_schema(
        operation_id='workhours',
        manual_parameters=[scheme.date_param],
        operation_description='Return workhours of employee with given id or workhours of current user if no id was given',
        responses={200: scheme.WorkhoursListReponse()}
    )
    def get(self, request, **kwargs):
        # if no id then access current user data
        employee_id = kwargs.get('employee_id', None)

        # Employer can access all users data and employee can only access his own
        if not self.has_permission(request.user) and (employee_id != None and employee_id != request.user.pk):
            serializer = MessageSerializer(Message('Only employers can access this endpoint'))
            return JsonResponse(serializer.data, safe=False, status=403)
           
        employee = None
        if employee_id != None:
            try:
                employee = User.objects.get(pk=employee_id)
            except ObjectDoesNotExist:
                serializer = MessageSerializer(Message('No employee with given id exists'))
                return JsonResponse(serializer.data, safe=False, status=404)
        else:
            employee = request.user
        return self.get_employee_workhours(employee)
        