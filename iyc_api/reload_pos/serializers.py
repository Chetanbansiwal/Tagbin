from rest_framework import serializers
from reload_pos.models import FranchiseData


class FranchiseDataSerializer(serializers.HyperlinkedModelSerializer):
    #owner = serializers.ReadOnlyField(source='owner.username')
    #highlight = serializers.HyperlinkedIdentityField(view_name='snippet-highlight', format='html')

    class Meta:
        model = FranchiseData
        fields = ('url', 'franchise_name','organization','mobile_no')
 