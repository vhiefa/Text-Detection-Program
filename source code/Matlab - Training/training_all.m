function training_all(file_input)
    [numData,txtData,rawData] =xlsread(file_input,1); %numData is number-type data, txtData is string-type data, rawData is all-type data
    [numData2,txtData2,rawData2] =xlsread(file_input,2); %numData is number-type data, txtData is string-type data, rawData is all-type data
    datainput = cell2mat(rawData(:,:)); 
    label = cell2mat(rawData2(:,:)); 
    datatarget = alphabet2number(label);
    
    neuronZ = [75];%this is the number of neuron for hidden layer
    parameterLR =[0.01];%learning rate
    parameterMaxError = [0.0001];
    parameterMaxEpoch = 5000;
    
    %TRAINING
	bp_train(datainput, datatarget, [size(datainput,2), neuronZ, size(datatarget,2)], parameterLR,parameterMaxError,parameterMaxEpoch);