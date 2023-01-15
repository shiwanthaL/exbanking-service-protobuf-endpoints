package com.yolo.practical.exbankingservice;

import com.yolo.practical.bankingservice.proto.CreateUserRequest;
import com.yolo.practical.bankingservice.proto.CreateUserResponse;
import com.yolo.practical.exbankingservice.repository.CreateUserRepository;
import com.yolo.practical.exbankingservice.service.BankingService;
import io.grpc.internal.testing.StreamRecorder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.TimeUnit;

@ExtendWith(MockitoExtension.class)
class ExbankingServiceProtobufEndpointsApplicationTests {

	private BankingService bankingService;

	@Mock
	private CreateUserRepository mockCreateUserRepository;

	@BeforeEach
	public void setup(){
		bankingService = new BankingService(mockCreateUserRepository);
	}

	@Test
	void mockCeateUserTest() throws Exception {
		CreateUserRequest request = CreateUserRequest.newBuilder().
				setFullName("Krashanth Yolo").
				setEmail("Krashanth@yolo.com").setPassport("N82292").build();


		CreateUserResponse response = CreateUserResponse.newBuilder().
				setFullName(request.getFullName()).setAccountNo("6304885392759266").
				setIbanNo("MT19PFST85193Ta8B3m8ip1B352V1G0").setSwiftCode("West135").
				setBankName("Trallia").setBranchName("Alichester").setBalance(34877).
				setEmail(request.getEmail()).setPassport(request.getPassport()).build();

		when(mockCreateUserRepository.addAccount(request)).thenReturn(response);
		StreamRecorder<CreateUserResponse> responseObserver = StreamRecorder.create();
		bankingService.createUser(request, responseObserver);

		if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
			fail("The call did not terminate in time");
		}
		//Validate response not generate exceptions
		assertNull(responseObserver.getError());
		List<CreateUserResponse> results = responseObserver.getValues();
		//Validate Unery calls always response single response at a time
		assertEquals(1, results.size());

		CreateUserResponse userAccount = results.get(0);

		assertEquals(userAccount.getFullName(), request.getFullName());
		assertEquals(userAccount.getAccountNo(), "6304885392759266");
	}

}
