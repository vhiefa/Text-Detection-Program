function MSE = calculate_MSE(Y_output, datatarget)
    number_neuronY = size(Y_output,2);
    totalData = size(Y_output,1);
    sikmaMSE=0;
    for iData=1:totalData
       Error=0;
       for j=1:number_neuronY
          Error = Error+(datatarget(iData,j)-Y_output(iData,j))^2;
       end
       MSE_each_data(iData) = Error/number_neuronY;
       sikmaMSE = sikmaMSE + MSE_each_data(iData);
    end
    MSE = sikmaMSE/totalData;