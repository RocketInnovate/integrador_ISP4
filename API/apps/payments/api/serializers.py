from rest_framework import serializers
from django.contrib.auth.models import User
from apps.payments.models import Payment

class PaymentSerializer(serializers.ModelSerializer):
    
    class Meta:
        user = serializers.StringRelatedField()
        model = Payment
        fields = ['id', 'user', 'method', 'price', 'status']