from drf_yasg import openapi
from api.serializers import WorkHoursSerializer, UserProfileSerializer
from rest_framework import serializers

class PaginatedListResponse(serializers.Serializer):
    count = serializers.IntegerField(read_only=True)
    next = serializers.CharField(max_length=500, read_only=True)
    previous = serializers.CharField(max_length=500, read_only=True)

class EmployeeListReponse(PaginatedListResponse):
    results = UserProfileSerializer(many=True)

class WorkhoursListReponse(PaginatedListResponse):
    results = WorkHoursSerializer(many=True)


date_param = openapi.Parameter(
                                'date', 
                                in_=openapi.IN_QUERY, 
                                description='Timestamp in miliseconds',
                                type=openapi.TYPE_INTEGER)

employee_query_param = openapi.Parameter(
                                'query', 
                                in_=openapi.IN_QUERY, 
                                description='Filter list by username of firstname or lastname with given query',
                                type=openapi.TYPE_STRING)