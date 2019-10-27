from django.http import JsonResponse
from django.contrib.auth.models import User
from rest_framework.views import APIView
from drf_yasg.utils import swagger_auto_schema
from django.http import HttpResponse, JsonResponse
from rest_framework.permissions import IsAuthenticated
from api.serializers import UserProfileSerializer, MessageSerializer
from api.utils import Message
import api.scheme as scheme
from rest_framework.pagination import LimitOffsetPagination

class UserGroupsView(APIView):
    permission_classes = (IsAuthenticated,)
  
    @swagger_auto_schema(
        operation_id='groups',
        operation_description='Return user groups',
        responses={200: 'String array with groups names'}
    )
    def get(self, request):
        groups = request.user.groups.values_list('name', flat = True)
        groups_names_list = list(groups)
        return JsonResponse(groups_names_list, safe=False, status=200)                                    

class EmployeesView(APIView):
    permission_classes = (IsAuthenticated,)

    def has_permission(self, user):
        return user.groups.filter(name='Employer').exists() or user.is_superuser
    
    def make_paginated_response(self, queryset):
        paginator = LimitOffsetPagination()
        queryset = paginator.paginate_queryset(queryset, self.request)
        serializer = UserProfileSerializer(queryset, many=True)
        return paginator.get_paginated_response(serializer.data)

    def filter_data(self, queryset):
        query = self.request.query_params.get('query', None)
        if query is not None:
            queryset = queryset.filter(username__contains=query)  | queryset.filter(first_name__contains=query) | queryset.filter(last_name__contains=query)
        return queryset
  
    @swagger_auto_schema(
        operation_id='employee',
        manual_parameters=[scheme.employee_query_param],
        operation_description='Return all employees',
        responses={200: scheme.EmployeeListReponse()}
    )
    def get(self, request):
        if self.has_permission(request.user):
            employees = User.objects.filter(groups__name='Employee')
            employees = self.filter_data(employees)
            return self.make_paginated_response(employees)
        else:
            serializer = MessageSerializer(Message('Only employers can access this endpoint'))
            return JsonResponse(serializer.data, safe=False, status=403)