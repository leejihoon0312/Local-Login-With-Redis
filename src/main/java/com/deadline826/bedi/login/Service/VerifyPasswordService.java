package com.deadline826.bedi.login.Service;



import com.deadline826.bedi.login.Domain.Dao.TempPasswordDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class VerifyPasswordService {

    private final TempPasswordDao tempPasswordDao;

    public void makeTempPasswordStorage(String email, String password){

        tempPasswordDao.createPasswordCertification(email, password);

    }

    public String getTempPassword(String email){

        return tempPasswordDao.getPasswordCertification(email);

    }

    public void removeTempPassword(String email){

        tempPasswordDao.removePasswordCertification(email);

    }



}
