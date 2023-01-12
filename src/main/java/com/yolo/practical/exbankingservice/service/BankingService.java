package com.yolo.practical.exbankingservice.service;

import com.github.javafaker.Faker;
import com.google.protobuf.Empty;
import com.yolo.practical.bankingservice.proto.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Banking Server Protobuf Service
 * Service end points has defined as per assignment request and additional 2 endpoints defined for test automation data cleanup
 */
@GrpcService
public class BankingService extends BankingServiceGrpc.BankingServiceImplBase {
    private Faker data = new Faker();
    private final List<CreateUserResponse> accountHolders = new ArrayList<>();
    private com.google.protobuf.Empty Empty;

    /**
     * Define rpc endpoint for create user account service call from server side
     * @param request
     * @param response
     */
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


    /**
     * Define rpc endpoint for get balance details for given customer
     * @param request
     * @param response
     */
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

    /**
     * Define rpc endpoint for customer deposit to own account
     * @param request
     * @param response
     */
    @Override
    public void deposit(DepositRequest request, StreamObserver<DepositResponse> response) {
        long prior_amount = 0; long current_balance = 0;
        if(accountHolders.get(0).getFullName().equals(request.getFullName())){
            prior_amount = accountHolders.get(0).getBalance();
            current_balance = accountHolders.get(0).getBalance() + request.getAmount();
            //TODO (original object need to update accordingly)
        }
        DepositResponse deposit = DepositResponse.newBuilder().
                setFullName(request.getFullName()).setPriorAmount(prior_amount).setCurrentBalance(current_balance).build();
        response.onNext(deposit);
        response.onCompleted();

    }

    /**
     * Define rpc endpoint for customer withdraw from own account
     * @param request
     * @param response
     */
    @Override
    public void withdraw(WithdrawRequest request, StreamObserver<WithdrawResponse> response) {
        long prior_amount = 0; long current_balance = 0;
        if(accountHolders.get(0).getFullName().equals(request.getFullName())){
            prior_amount = accountHolders.get(0).getBalance();
            current_balance = accountHolders.get(0).getBalance() - request.getAmount();
            //TODO (original object need to update accordingly)
        }
        WithdrawResponse withdraw = WithdrawResponse.newBuilder().
                setFullName(request.getFullName()).setPriorAmount(prior_amount).setCurrentBalance(current_balance).build();
        response.onNext(withdraw);
        response.onCompleted();
    }

    /**
     * Defien rpc endpoint for money transfer to another account
     * @param request
     * @param response
     */
    @Override
    public void send(SendRequest request, StreamObserver<SendResponse> response) {
        long prior_amount = 0; long current_balance = 0;
        if(accountHolders.get(0).getFullName().equals(request.getFullName())){
            prior_amount = accountHolders.get(0).getBalance();
            current_balance = accountHolders.get(0).getBalance() - request.getAmount();
            //TODO (throw error if transfer amount higher that available balance)
            //TODO (original object need to update accordingly)
        }
        SendResponse send = SendResponse.newBuilder().
                setFromAccount(accountHolders.get(0).getAccountNo()).
                setBeneficiaryAccount(request.getSenderAccount()).
                setTransactionRemark(request.getRemark()).setReferenceNo(data.number().numberBetween(1000,9999)+"REF").
                setTransferAmount(request.getAmount()).setCurrentBalance(current_balance).build();
        response.onNext(send);
        response.onCompleted();
    }

    /**
     * Define endpoint for bank purpose to see all registered customers in bank
     * @param request
     * @param response
     */
    @Override
    public void getUsers(Empty request, StreamObserver<GetUsersResponse> response) {
        response.onNext(GetUsersResponse.newBuilder().addAllUsers(accountHolders).build());
        response.onCompleted();
    }

    /**
     * Define endpoint for bank purpose to close requested customer account
     * @param request
     * @param response
     */
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
