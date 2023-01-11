package com.yolo.practical.exbankingservice.service;

import com.github.javafaker.Faker;
import com.google.protobuf.Empty;
import com.yolo.practical.bankingservice.proto.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

@GrpcService
public class BankingService extends BankingServiceGrpc.BankingServiceImplBase {
    private Faker data = new Faker();
    private final List<CreateUserResponse> accountHolders = new ArrayList<>();
    private com.google.protobuf.Empty Empty;

    @Override
    public void createUser(CreateUserRequest request, StreamObserver<CreateUserResponse> response) {

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

        accountHolders.add(createUser);
        response.onNext(createUser);
        response.onCompleted();
    }


    @Override
    public void getBalance(GetBalanceRequest request, StreamObserver<GetBalanceResponse> response) {
        String account_no = null; long balance=0;
        if(accountHolders.get(0).getFullName().equals(request.getFullName())){
            account_no = accountHolders.get(0).getAccountNo();
            balance = accountHolders.get(0).getBalance();
        }
        GetBalanceResponse getBalance = GetBalanceResponse.newBuilder().
                setFullName(request.getFullName()).setAccountNo(account_no).setBalance(balance).build();
        response.onNext(getBalance);
        response.onCompleted();
    }

    @Override
    public void getUsers(Empty request, StreamObserver<GetUsersResponse> response) {
        response.onNext(GetUsersResponse.newBuilder().addAllUsers(accountHolders).build());
        response.onCompleted();
    }

    @Override
    public void closeAccount(CloseAccountRequest request, StreamObserver<Empty> response) {
        Iterator<CreateUserResponse> it_user = accountHolders.iterator();
        while (it_user.hasNext()){
            if(it_user.next().getFullName().equals(request.getFullName())){
                it_user.remove();
            }
        }
        response.onNext(Empty);
        response.onCompleted();
    }
}
