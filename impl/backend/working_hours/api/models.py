from django.db import models
from django.contrib.auth.models import User


"""Dzien pracy - od zeskanowania kodu do ponownego zeskanowania
"""
class WorkDay(models.Model):
    user = models.ForeignKey(User, on_delete=models.SET_NULL, null=True)
    started = models.DateTimeField(auto_now_add=True)
    finished = models.DateTimeField(null=True)

    class Meta:
        app_label = 'api'


