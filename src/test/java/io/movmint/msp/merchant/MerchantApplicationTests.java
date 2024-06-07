package io.movmint.msp.merchant;

import io.movmint.msp.merchant.data.repository.BusinessRepository;

import io.movmint.msp.merchant.data.repository.UserRepository;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(classes = {
		MerchantApplication.class,
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class MerchantApplicationTests {

	@MockBean
	protected BusinessRepository businessRepository;

	@MockBean
	protected UserRepository userRepository;

}
