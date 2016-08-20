kerubApp.controller('VirtualNetworkTab', function($scope, $log, $uibModal, socket, appsession) {
	$scope.vnets = {};
	$scope.newVirtualNetwork = function() {
	   var modalInstance = $uibModal.open({
			templateUrl : 'NewVirtualNetworkWizard.html',
			controller : NewVirtualNetworkWizard
		});
	};
	$scope.refresh = function() {
		appsession.get('/s/r/vnet').success(function(vnets) {
			$scope.vnets = vnets;
		});
	};

	$scope.refresh();
	socket.subscribe('/vnet', function() {
		$log.info("refresh");
		$scope.refresh();
	});
});
