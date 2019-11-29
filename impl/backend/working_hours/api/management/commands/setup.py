from django.core.management.base import BaseCommand, CommandError
from django.contrib.auth.models import User
from django.core.exceptions import ObjectDoesNotExist
import os
import api.models as models
from django.contrib.auth.models import Group, Permission

DEFAULT_ADMIN_USERNAME = 'admin'
DEFAULT_ADMIN_PASSWORD = 'admin'

SAMPLE_EMPLEYEE_COUNT = 10

DEFAULT_EMPLOYER_USERNAME = 'pracodawca'
DEFAULT_EMPLOYER_PASSWORD = 'pracodawca'

DEFAULT_EMPLOYEE_USERNAME = 'pracownik'
DEFAULT_EMPLOYEE_PASSWORD = 'pracownik'

GROUPS_PERMISSIONS = {
    'Employer': {
        models.WorkDay: ['add', 'change', 'delete', 'view'],
    },
    'Employee': {
        models.WorkDay: ['add', 'change', 'view'],
    },
}

class Command(BaseCommand):
    help = 'Commands that should be run before running application. It setups it and add required data to database'

    def handle(self, *args, **kwargs):
        self.migrate_db()
        self.create_groups()
        self.create_default_admin_user()
        self.create_default_employer_user()
        self.create_default_employee_user()
        self.create_sample_employees_users()

    def migrate_db(self):
        os.system('python manage.py makemigrations')
        os.system('python manage.py migrate')
        os.system('python manage.py makemigrations api')
        os.system('python manage.py migrate api')
       
    def create_default_admin_user(self):
        user = None
        try:
            user = User.objects.get(username=DEFAULT_ADMIN_USERNAME)
            self.stdout.write('UPDATE default admin user')
        except ObjectDoesNotExist:
            self.stdout.write('CREATE default admin user')
            user = User(username=DEFAULT_ADMIN_USERNAME)
        user.set_password(DEFAULT_ADMIN_PASSWORD)
        user.is_superuser = True
        user.is_staff = True
        user.save()
       
    def create_default_employer_user(self):
        user = None
        try:
            user = User.objects.get(username=DEFAULT_EMPLOYER_USERNAME)
            self.stdout.write('UPDATE default employer user')
        except ObjectDoesNotExist:
            self.stdout.write('CREATE default employer user')
            user = User(username=DEFAULT_EMPLOYER_USERNAME)
        user.set_password(DEFAULT_EMPLOYER_PASSWORD)
        user.is_superuser = False
        user.is_staff = True
        user.save()
        group = Group.objects.get(name='Employer') 
        group.user_set.add(user)

    def create_default_employee_user(self):
        user = None
        try:
            user = User.objects.get(username=DEFAULT_EMPLOYEE_USERNAME)
            self.stdout.write('UPDATE default employee user')
        except ObjectDoesNotExist:
            self.stdout.write('CREATE default employee user')
            user = User(username=DEFAULT_EMPLOYEE_USERNAME)
        user.set_password(DEFAULT_EMPLOYEE_PASSWORD)
        user.is_superuser = False
        user.is_staff = True
        user.save()
        group = Group.objects.get(name='Employee') 
        group.user_set.add(user)

    def create_sample_employees_users(self):
        self.stdout.write('CREATING / UPDATING sample employees users')
        for i in range(0, 10):
            user = None
            username = DEFAULT_EMPLOYEE_USERNAME + '_' + str(i)
            try:
                user = User.objects.get(username=username)
            except ObjectDoesNotExist:
                user = User(username=username)
            user.set_password(DEFAULT_EMPLOYEE_PASSWORD)
            user.is_superuser = False
            user.is_staff = True
            user.save()
            group = Group.objects.get(name='Employee') 
            group.user_set.add(user)

    def create_groups(self):
        # Loop groups
        for group_name in GROUPS_PERMISSIONS:
            # Get or create group
            group, created = Group.objects.get_or_create(name=group_name)
            # Loop models in group
            for model_cls in GROUPS_PERMISSIONS[group_name]:
                # Loop permissions in group/model
                for perm_index, perm_name in \
                        enumerate(GROUPS_PERMISSIONS[group_name][model_cls]):
                    # Generate permission name as Django would generate it
                    codename = perm_name + "_" + model_cls._meta.model_name
                    try:
                        # Find permission object and add to group
                        perm = Permission.objects.get(codename=codename)
                        group.permissions.add(perm)
                        self.stdout.write("Adding "
                                          + codename
                                          + " to group "
                                          + group.__str__())
                    except Permission.DoesNotExist:
                        self.stdout.write(codename + " not found")
