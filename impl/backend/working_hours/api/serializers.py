from rest_framework import serializers
from django.core.exceptions import ObjectDoesNotExist

class VersionSerializer(serializers.Serializer):
    version = serializers.CharField(max_length=100)

class MessageSerializer(serializers.Serializer):
    message = serializers.CharField(max_length=1000)

class CodeSerializer(serializers.Serializer):
    code = serializers.CharField(max_length=6, min_length=6)

class UserProfileSerializer(serializers.Serializer):
    id = serializers.IntegerField(read_only=True)
    username = serializers.CharField(max_length=200, read_only=True)
    email = serializers.EmailField(max_length=400, read_only=True)
    first_name = serializers.CharField(max_length=200, read_only=True)
    last_name = serializers.CharField(max_length=200, read_only=True)

class WorkHoursSerializer(serializers.Serializer):
    id = serializers.IntegerField(read_only=True)
    started = serializers.DateTimeField(format="%Y-%m-%dT%H:%M:%S", read_only=False)
    finished = serializers.DateTimeField(format="%Y-%m-%dT%H:%M:%S", read_only=False)
    user = UserProfileSerializer(many=False)