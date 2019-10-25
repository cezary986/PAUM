from django.conf import settings
from django.utils import timezone
import logging
import json

# Get an instance of a logger
logger = logging.getLogger(__name__)

'''
Middleware wypisujący do konsoli body requestu i odpowiedzi jeśli była poprawna. 
Bardziej do debugowania i podglądania czy dobrze wysyłanym dane
'''
class AccessLogsMiddleware(object):

    def __init__(self, get_response=None):
        self.get_response = get_response
        # One-time configuration and initialization.

    def __call__(self, request):
        # create session
        if not request.session.session_key:
            request.session.create()

        response = self.get_response(request)

        request_log_data = {}
        request_log_data['path'] = request.path
        if not 'admin/' in request.path:
            request_log_data['token'] = request.headers.get('Authorization')
            if request_log_data['token'] != None:
                request_log_data['token'] = request_log_data['token'].replace('Bearer ', '')

            if request.method == 'POST':
                request_log_data["body"] = dict(request.POST.copy())
        
            logger.info(json.dumps(request_log_data, indent=4, sort_keys=True))
            print('Request data: ' + json.dumps(request_log_data, indent=4) + '\n')

            response_log_data = {}

            response_log_data['status_code'] = response.status_code
            # poprawna odpowiedź brak błędów
            if response.status_code >= 200 | response.status_code <= 206:
                response_log_data['body'] = json.loads(response.content.decode("utf-8"))
                print('Response data: ' + json.dumps(response_log_data, indent=4))
        return response