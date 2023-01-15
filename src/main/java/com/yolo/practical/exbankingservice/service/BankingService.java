package com.yolo.practical.exbankingservice.service;

import com.github.javafaker.Faker;
import com.google.protobuf.Empty;
import com.yolo.practical.bankingservice.proto.*;
import com.yolo.practical.exbankingservice.repository.CreateUserRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Banking Server Protobuf Service
 * Service end points has defined as per assignment request and additional 2 endpoints defined for test automation data cleanup
 */
@RequiredArgsConstructor
@GrpcService
public class BankingService extends BankingServiceGrpc.BankingServiceImplBase {
    private Faker data = new Faker();
    private List<CreateUserResponse> accountHolders = new ArrayList<>();
    private com.google.protobuf.Empty Empty;
    private final CreateUserRepository createUserRepository;

    Logger log = LoggerFactory.getLogger(BankingService.class);
    /**
     * Define rpc endpoint for create user account service call from server side
     * @param request
     * @param response
     */
    @Override
    public void createUser(CreateUserRequest request, StreamObserver<CreateUserResponse> response) {

//        CreateUserResponse createUser = CreateUserResponse.newBuilder().
//
//                setFullName(request.getFullName()).
//                setEmail(request.getEmail()).
//                setPassport(request.getPassport()).
//                setAccountNo(data.finance().creditCard()).
//                setIbanNo(data.finance().iban()).
//                setSwiftCode(data.address().cityPrefix()+data.number().digits(3)).
//                setBankName(data.company().name()).
//                setBranchName(data.address().cityName()).
//                setBalance(data.number().numberBetween(10000,50000)).build();
//        log.info("Create response payload with account details : create_user endpoint");
//        accountHolders.add(createUser);

        CreateUserResponse createUser = createUserRepository.addAccount(request);
        accountHolders = createUserRepository.getAccount();
        log.info("Persist customer record in memory : create_user endpoint");
        response.onNext(createUser);
        log.info("Create response and send to client : create_user endpoint");
        response.onCompleted();
        log.info("Connection terminated from server side : create_user endpoint");
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
            log.info("Search and filter customer bank details : get_balance endpoint");
        }
        GetBalanceResponse getBalance = GetBalanceResponse.newBuilder().
                setFullName(request.getFullName()).setAccountNo(account_no).setBalance(balance).build();
        log.info("Create response payload with balance amount : get_balance endpoint");
        response.onNext(getBalance);
        log.info("Create response and send to client : get_balance endpoint");
        response.onCompleted();
        log.info("Connection terminated from server side : get_balance endpoint");
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
            log.info("Search customer account and make deposit given amount : deposit endpoint");
        }
        DepositResponse deposit = DepositResponse.newBuilder().
                setFullName(request.getFullName()).setPriorAmount(prior_amount).setCurrentBalance(current_balance).build();
        log.info("Create response payload with deposit details : deposit endpoint");
        response.onNext(deposit);
        log.info("Create response and send to client : deposit endpoint");
        response.onCompleted();
        log.info("Connection terminated from server side : deposit endpoint");
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
            log.info("Search customer account and make withdraw given amount : withdraw endpoint");
        }
        WithdrawResponse withdraw = WithdrawResponse.newBuilder().
                setFullName(request.getFullName()).setPriorAmount(prior_amount).setCurrentBalance(current_balance).build();
        log.info("Create response payload with withdraw details : withdraw endpoint");
        response.onNext(withdraw);
        log.info("Create response and send to client : withdraw endpoint");
        response.onCompleted();
        log.info("Connection terminated from server side : withdraw endpoint");
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
            log.info("Search customer account and money transfer to given account : send endpoint");
        }
        SendResponse send = SendResponse.newBuilder().
                setFromAccount(accountHolders.get(0).getAccountNo()).
                setBeneficiaryAccount(request.getSenderAccount()).
                setTransactionRemark(request.getRemark()).setReferenceNo(data.number().numberBetween(1000,9999)+"REF").
                setTransferAmount(request.getAmount()).setCurrentBalance(current_balance).build();
        log.info("Create response payload with transfer details : send endpoint");
        response.onNext(send);
        log.info("Create response and send to client : send endpoint");
        response.onCompleted();
        log.info("Connection terminated from server side : send endpoint");
    }

    /**
     * Define endpoint for bank purpose to see all registered customers in bank
     * @param request
     * @param response
     */
    @Override
    public void getUsers(Empty request, StreamObserver<GetUsersResponse> response) {
        response.onNext(GetUsersResponse.newBuilder().addAllUsers(accountHolders).build());
        log.info("Search all account holders and display : users endpoint");
        response.onCompleted();
        log.info("Connection terminated from server side : users endpoint");
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
                log.info("Search account and close it : close_account endpoint");
            }
        }
        response.onNext(Empty);
        response.onCompleted();
        log.info("Connection terminated from server side : close_account endpoint");
    }
}
