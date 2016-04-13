var NewVirtualDiskWizard = function($scope, $uibModalInstance, $log, appsession, uuid4, size, expectations, FileUploader) {
	$log.info('file uploader', FileUploader);

	var id = uuid4.generate();

	$scope.uploader = new FileUploader( {
		url : 's/r/virtual-storage/load/' + id
	} );

	$scope.autoname = true;

	$scope.disk = {
		'@type' : 'vstorage',
		id : id,
		name : 'disk-'+id,
		size : size.toSize('16 GB'),
		userFriendlySize : function(newSize) {
			$log.debug(newSize);
			if(newSize) {
				$scope.disk.size = size.toSize(newSize);
			}
			return size.humanFriendlySize($scope.disk.size);
		},
		expectations : []
	};

	$scope.uploader.onAfterAddingFile = function(item) {
		$log.info('file added', item.file.name, item.file.size);
		if($scope.autoname) {
			$scope.disk.name = item.file.name
		}
		$scope.disk.size = item.file.size;
	}

	$scope.nameChanged = function() {
		$scope.autoname = false;
	}

	/**
	 * gettersetter for disk size
	 */
	$scope.diskSize = function(val) {
		if(val) {
			$log.info(val);
		} else {
		}
	}

	$scope.addStorage = function() {
		appsession.put('s/r/virtual-storage', $scope.disk).success(function(result) {
			if($scope.uploader.queue.length > 0) {
				$scope.uploader.uploadAll();
				$scope.uploader.onSuccessItem = function(fileItem, response, status, headers) {
					$uibModalInstance.dismiss();
				}
			} else {
	        	$uibModalInstance.dismiss();
			}
		});
	};

	$scope.close = function() {
		$uibModalInstance.dismiss();
	};

	$scope.storageExpectationsOpen = false;
	$scope.storageExpectationFormOpen = false;
	$scope.diskExpectations = filterValues(expectations, function(exp) { return exp.virtTypes.includes("vstorage") } );
	$scope.openExpectationForm = function(expType) {
		$scope.storageExpectationsOpen = false;
		$scope.storageExpectationFormOpen = true;
		$scope.newExpectation = expType;
	}

}
