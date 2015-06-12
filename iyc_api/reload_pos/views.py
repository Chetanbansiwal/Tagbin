from rest_framework import viewsets
from reload_pos.models import FranchiseData
from reload_pos.serializers import FranchiseDataSerializer

class FranchiseDataViewSet(viewsets.ModelViewSet):
	queryset = FranchiseData.objects.all()
	serializer_class = FranchiseDataSerializer