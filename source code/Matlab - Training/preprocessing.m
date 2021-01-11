function preprocessing(folder_name)

    myFolder = folder_name;
    myFiles = dir(fullfile(myFolder, '*.png'));
    numberOfFiles = size(myFiles,1);
    
    tinggi=14;
    lebar = 8;
    for dataKe = 1:numberOfFiles
            citra_karakter=imread(fullfile(myFolder,myFiles(dataKe).name));
            namaFile=myFiles(dataKe).name;
            namaKarakter = namaFile(1);
            label(dataKe) = namaKarakter;
            citra_karakter = im2bw(citra_karakter); 
            citra_karakter = imresize(citra_karakter,[tinggi lebar]);
            k=1;
            for i=1:tinggi
                for j=1:lebar
                    feature(dataKe,k) = citra_karakter(i,j);
                    k=k+1;
                end
            end
            
    end
    xlswrite(['featuresnlabel-',num2str(tinggi),'x',num2str(lebar),'.xlsx'],feature,1);
    xlswrite(['featuresnlabel-',num2str(tinggi),'x',num2str(lebar),'.xlsx'],label',2);


        
        
