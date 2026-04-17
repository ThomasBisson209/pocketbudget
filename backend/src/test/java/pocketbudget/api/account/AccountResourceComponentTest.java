package pocketbudget.api.account;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.Test;
import pocketbudget.api.exceptions.mappers.AccountNotFoundExceptionMapper;
import pocketbudget.application.account.AccountAssembler;
import pocketbudget.application.account.AccountService;
import pocketbudget.domain.account.AccountRepository;
import pocketbudget.domain.transaction.TransactionRepository;
import pocketbudget.infra.persistence.inMemory.InMemoryAccountRepository;
import pocketbudget.infra.persistence.inMemory.InMemoryTransactionRepository;

import static org.junit.jupiter.api.Assertions.*;

class AccountResourceComponentTest extends JerseyTest {

    @Override
    protected Application configure() {
        ResourceConfig config = new ResourceConfig(AccountResource.class, AccountNotFoundExceptionMapper.class);
        // Inject a test username so AccountResource.userId() returns a non-null value
        config.register((ContainerRequestFilter) ctx -> ctx.setProperty("username", "testUser"));
        config.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(new InMemoryAccountRepository()).to(AccountRepository.class);
                bind(new InMemoryTransactionRepository()).to(TransactionRepository.class);
                bindAsContract(AccountAssembler.class);
                bindAsContract(AccountService.class);
            }
        });
        return config;
    }

    @Test
    void givenNoAccounts_whenGetAllAccounts_thenReturnsEmptyList() {
        Response response = target("/accounts").request().get();
        assertEquals(200, response.getStatus());
        assertEquals("[]", response.readEntity(String.class));
    }

    @Test
    void givenValidPayload_whenCreateAccount_thenReturns201WithAccountDto() {
        String payload = """
            {"name": "Test Account", "type": "CHECKING", "initialBalance": 500.0}
            """;
        Response response = target("/accounts").request()
            .post(Entity.entity(payload, MediaType.APPLICATION_JSON));
        assertEquals(201, response.getStatus());
        String body = response.readEntity(String.class);
        assertTrue(body.contains("Test Account"));
        assertTrue(body.contains("CHECKING"));
    }

    @Test
    void givenUnknownAccountId_whenGetAccount_thenReturns404() {
        Response response = target("/accounts/non-existent-id").request().get();
        assertEquals(404, response.getStatus());
    }
}
