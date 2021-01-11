%function to do a prediction
function Y = bp_predict(datainput, weight, networkarchitecture)
   weightV= weight{1};
   weightW = weight{2};
   number_neuronX = networkarchitecture(1);
   number_neuronZ = networkarchitecture(2);
   number_neuronY = networkarchitecture(3);
   for i=1:number_neuronZ
       sikmaV =0;
       for j=2:number_neuronX+1
         sikmaV = sikmaV + datainput(j-1)*weightV( i, j);
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
       Y(i) = 1/(1+ exp(-Ynet(i))); %1*weightW(i, 1) + sikmaW;%
   end