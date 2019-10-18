from django.http.response import HttpResponse
from django.http.response import HttpResponseForbidden
from django.http.response import HttpResponseNotAllowed
from functools import wraps

def login_required_view(func):
    """
    Decorator for throwing an 403 error when user is not logged in
    """
    def wrapper(self, request, *args, **kwargs):
        if not request.user.is_authenticated:            
            return HttpResponse("You must be logged in", status=401)
        else:
            return func(self, request, *args, **kwargs)
        
    return wrapper
