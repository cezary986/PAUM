from django.urls import path
from django.conf.urls import url
from api.views import VersionView, CodeView, EmployeesView, WorkHoursView
from rest_framework.authtoken import views
from django.urls import include
from rest_auth import views as rest_auth_views
from rest_framework_simplejwt.views import (
    TokenObtainPairView,
    TokenRefreshView,
)

urlpatterns = [
    path('version/', VersionView.as_view(), name='version'),

    path('code/', CodeView.as_view(), name='code'),

    path('employee/', EmployeesView.as_view(), name='employee'),
    path('employee/<int:employee_id>/work_hours/', WorkHoursView.as_view(), name='employee_work_hours'),

    path('auth/login/', TokenObtainPairView.as_view(), name='token_obtain_pair'),
    path('auth/refresh/', TokenRefreshView.as_view(), name='token_refresh'),
    path('user/profile/', rest_auth_views.UserDetailsView.as_view(), name="profile")
]