from django.db import models
from django.contrib.auth.models import BaseUserManager, AbstractBaseUser, PermissionsMixin


class UserManager(BaseUserManager):
    def _create_user(self,  email, password, is_staff, is_superuser, **extra_fields):
        user = self.model(
            email=email,
            is_staff=is_staff,
            is_superuser=is_superuser,
            **extra_fields
        )
        user.set_password(password)
        user.save(using=self.db)
        return user

    def create_user(self, email, password=None, **extra_fields):
        return self._create_user(dni, name, last_name, email, phone, password, is_staff= False, is_superuser=False, **extra_fields)

    def create_superuser(self, email, phone=None, password=None, **extra_fields):
        return self._create_user( email, password, is_staff=True, is_superuser=True, **extra_fields)


class User(AbstractBaseUser, PermissionsMixin):
    email = models.EmailField('Correo Electrónico',max_length=255, unique=True,)
    is_premium = models.BooleanField(default=False)
    is_staff = models.BooleanField(default=False)

    objects = UserManager()

    class Meta:
        verbose_name = 'Usuario'
        verbose_name_plural = 'Usuarios'

    USERNAME_FIELD = 'email'
    REQUIRED_FIELDS = []

    def __str__(self):
        return self.email
