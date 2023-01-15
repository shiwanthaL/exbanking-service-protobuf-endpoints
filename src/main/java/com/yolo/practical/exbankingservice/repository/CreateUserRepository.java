package com.yolo.practical.exbankingservice.repository;

import com.github.javafaker.Faker;
import com.yolo.practical.bankingservice.proto.CreateUserRequest;
import com.yolo.practical.bankingservice.proto.CreateUserResponse;
import com.yolo.practical.exbankingservice.service.BankingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class CreateUserRepository {
    private Faker data = new Faker();
    Logger log = LoggerFactory.getLogger(CreateUserRepository.class);
    private final List<CreateUserResponse> accountHolders = new ArrayList<>();


    public CreateUserResponse addAccount(CreateUserRequest request){
        CreateUserResponse createUser = CreateUserResponse.newBuilder().

                setFullName(request.getFullName()).
                setEmail(request.getEmail()).
                setPassport(request.getPassport()).
                setAccountNo(data.finance().creditCard()).
                setIbanNo(data.finance().iban()).
                setSwiftCode(data.address().cityPrefix()+data.number().digits(3)).
                setBankName(data.company().name()).
                setBranchName(data.address().cityName()).
                setBalance(data.number().numberBetween(10000,50000)).build();
        log.info("Create response payload with account details : create_user endpoint");
        accountHolders.add(createUser);

        return createUser;
    }

    public List<CreateUserResponse> getAccount(){
        return accountHolders;
    }
}
