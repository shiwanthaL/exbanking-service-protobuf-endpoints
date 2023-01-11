package com.yolo.practical.exbankingservice.service;

import com.github.javafaker.Faker;
import com.google.protobuf.Empty;
import com.yolo.practical.bankingservice.proto.BankingServiceGrpc;
import com.yolo.practical.bankingservice.proto.CreateUserRequest;
import com.yolo.practical.bankingservice.proto.CreateUserResponse;
import com.yolo.practical.bankingservice.proto.GetUsersResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.ArrayList;
import java.util.List;

@GrpcService
public class BankingService extends BankingServiceGrpc.BankingServiceImplBase {
    private Faker data;
    private final List<CreateUserResponse> accountHolders = new ArrayList<>();

    @Override
    public void getUsers(Empty request, StreamObserver<GetUsersResponse> response) {
        response.onNext(GetUsersResponse.newBuilder().addAllUsers(accountHolders).build());
        response.onCompleted();
    }

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

}
