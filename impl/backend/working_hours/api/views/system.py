from api import VERSION
from django.http import JsonResponse
from rest_framework.views import APIView
from drf_yasg.utils import swagger_auto_schema
from api.serializers import VersionSerializer
from django.conf import settings

class VersionView(APIView):
  
    @swagger_auto_schema(
        operation_id='version',
        operation_description='Return current API version',
        responses={200: VersionSerializer}
    )
    def get(self, request):
        serializer = VersionSerializer({'version': VERSION})
        return JsonResponse(serializer.data)
