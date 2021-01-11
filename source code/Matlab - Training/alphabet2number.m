function datatarget = alphabet2number(label)
    totalData = length(label);
    datatarget(totalData, 26) = 0;
    for i=1:totalData
        karakter = label(i);
        if strcmp (karakter,'A')
            datatarget(i,1)=1;
        end
        if strcmp (karakter, 'B')
            datatarget(i,2)=1;
            end
        if strcmp (karakter,'C')
            datatarget(i,3)=1;
            end
        if strcmp (karakter, 'D')
            datatarget(i,4)=1;
            end
        if strcmp (karakter,'E')
            datatarget(i,5)=1;
            end
        if strcmp (karakter, 'F')
            datatarget(i,6)=1;
            end
        if strcmp (karakter, 'G')
            datatarget(i,7)=1;
            end
        if strcmp (karakter, 'H')
            datatarget(i,8)=1;
            end
        if strcmp (karakter, 'I')
            datatarget(i,9)=1;
            end
        if strcmp (karakter, 'J')
            datatarget(i,10)=1;
            end
        if strcmp (karakter, 'K')
            datatarget(i,11)=1;
            end
        if strcmp (karakter, 'L')
            datatarget(i,12)=1;
            end
        if strcmp (karakter,'M')
            datatarget(i,13)=1;
            end
        if strcmp (karakter, 'N')
            datatarget(i,14)=1;
            end
        if strcmp (karakter, 'O')
            datatarget(i,15)=1;
            end
        if strcmp (karakter, 'P')
            datatarget(i,16)=1;
            end
        if strcmp (karakter, 'Q')
            datatarget(i,17)=1;
            end
        if strcmp (karakter, 'R')
            datatarget(i,18)=1;
            end
        if strcmp (karakter, 'S')
            datatarget(i,19)=1;
            end
        if strcmp (karakter, 'T')
            datatarget(i,20)=1;
            end
         if strcmp (karakter, 'U')
            datatarget(i,21)=1;
            end
         if strcmp (karakter, 'V')
            datatarget(i,22)=1;
            end
         if strcmp (karakter, 'W')
            datatarget(i,23)=1;
            end
         if strcmp (karakter, 'X')
            datatarget(i,24)=1;
            end
         if strcmp (karakter, 'Y')
            datatarget(i,25)=1;
            end
         if strcmp (karakter,'Z')
            datatarget(i,26)=1;
         end
    end

    