function bp_train(datainput, datatarget, network_architecture, LR, maxError, maxEpoch)

totalTrainingData = size (datainput,1);

number_neuronX = network_architecture(1);
number_neuronZ = network_architecture(2);
number_neuronY = network_architecture(3);

epoch=1;
MSE =1;
tic;


%generate random number to initiate weight V
for i=1:number_neuronZ
    for j=1:number_neuronX+1
        weightV(i,j)=0.01*randn(1);%rand(1); %
    end
end

%generate random number to initiate weight W
for i=1:number_neuronY
    for j=1:number_neuronZ+1
        weightW(i,j)=0.01*randn(1);%rand(1);% 
    end
end


while  epoch<=maxEpoch && (MSE>maxError) %  it will stop when epoch >= maxEpox OR MSE <=maxEror
    
      for iData=1:totalTrainingData
        %PHASE 1 : FORWARD
        for i=1:number_neuronZ
            sikmaV =0;
            for j=2:number_neuronX+1
                sikmaV = sikmaV + datainput(iData, j-1)*weightV( i, j);
            end
            Znet(i) = 1*weightV(i, 1) + sikmaV;
            Z(i) = 1/(1+ exp(-Znet(i))); %1*weightV(i, 1) + sikmaV;%
        end
        for i=1:number_neuronY
            sikmaW =0;
            for j=2:number_neuronZ+1
                sikmaW = sikmaW + Z(j-1)*weightW( i, j);
            end
            Ynet(i) = 1*weightW(i, 1) + sikmaW;
            Y(iData,i) = 1/(1+ exp(-Ynet(i))); %1*weightW(i, 1) + sikmaW;%
        end
        
        %PHASE 2 : BACKWARD
        for k=1:number_neuronY
            factor_d_output_unit(k) = (datatarget(iData,k)-Y(iData,k))*Y(iData,k)*(1-Y(iData,k));
            deltaW(k,1) = LR*factor_d_output_unit(k)*1; %1 is bias
            for j=2:number_neuronZ+1
                deltaW(k,j) = LR*factor_d_output_unit(k)*Z(j-1);
            end
        end
        dnet(1:number_neuronZ)=0;
        for i=2:number_neuronZ+1
            for j=1:number_neuronY
                dnet(i-1)=dnet(i-1) + factor_d_output_unit(j)*weightW(j,i);
            end
        end      
        for i=1:number_neuronZ
            factor_d_hidden_unit(i) = dnet(i)*Z(i)*(1-Z(i));
            deltaV(i,1)=LR*factor_d_hidden_unit(i)*1;
            for j=2:number_neuronX+1
                deltaV(i,j)=LR*factor_d_hidden_unit(i)*datainput(iData,j-1);
            end
        end
        %PHASE III : WEIGHT ADJUSTMENT
        for i=1:number_neuronZ
            for j=1:number_neuronX+1
                weightV(i,j)=weightV(i,j)+deltaV(i,j);
            end
        end
        for i=1:number_neuronY
            for j=1:number_neuronZ+1
                weightW(i,j)=weightW(i,j)+deltaW(i,j);
            end
        end
        
        Y(iData, :) = bp_predict(datainput(iData,:), {weightV weightW}, network_architecture);
        
      end   
      
    MSE = calculate_MSE(Y, datatarget);
    iteration(epoch)= MSE
    epoch=epoch+1;
end
t=toc;
weight_file_name = 'weight.xlsx';
result = {MSE, epoch-1, t, weight_file_name}

%WRITE TO EXCEL WEIGHT V
weight_V={'hidden/input (weight V)', '''1'};
for i=1:number_neuronX
    weight_V{1,i+2}=strcat('X',num2str(i));
end
for i=1:number_neuronZ
    weight_V{i+1,1}=strcat('Z',num2str(i));
    for j=1:number_neuronX+1
        weight_V{i+1,j+1} = weightV(i,j);
    end
end
xlswrite(weight_file_name,weight_V,1); %filename, data, sheet

%WRITE TO EXCEL WEIGHT W
weight_W = {'output/hidden(weight W)', '''1'};
for i=1:number_neuronZ
    weight_W{1,i+2}=strcat('Z',num2str(i));
end
for i=1:number_neuronY
    weight_W{i+1,1}=strcat('Y',num2str(i));
    for j=1:number_neuronZ+1
        weight_W{i+1,j+1} = weightW(i,j);
    end
end
xlswrite(weight_file_name,weight_W,2);
xlswrite('trainingCurve.xlsx',iteration');