from django.db import models

# Create your models here.

class FranchiseData(models.Model):
	franchise_name = models.CharField(max_length=100)
	organization = models.CharField(max_length=50)
	pan = models.CharField(max_length=20)
	email = models.CharField(max_length=50)
	office_landline_no = models.CharField(max_length=20)
	mobile_no = models.CharField(max_length=10)
	service_tax = models.TextField()
	created = models.DateTimeField(auto_now_add=True)
	verified_by = models.IntegerField(max_length=1, default=0)
	
