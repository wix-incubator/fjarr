package com.wixpress.fjarr.server;

import com.wixpress.fjarr.server.example.TestService;
import com.wixpress.fjarr.server.example.TestServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerMapping;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * @author alex
 * @since 12/24/12 1:00 PM
 */

@Configuration
public class ITConfiguration
{
    @Bean
    public RpcServiceRegistry registry()
    {
        return new RpcServiceRegistry();
    }

    @Bean
    public RpcServiceRegistration testRegistration(final TestServiceImpl testService)
    {
        return new RpcServiceRegistration()
        {
            @Override
            public void registerServices()
            {
                registerEndpoint(TestService.class, testService);
            }
        };
    }

    @Bean
    public HandlerAdapter rpcAdapter()
    {
        return new RpcServiceHandlerAdapter();
    }

    @Bean
    public HandlerMapping rpcMapping()
    {
        return new RpcServiceHandlerMapping();
    }

    @Bean
    public RpcProtocol rpcProtocol()
    {
        return mock(RpcProtocol.class);
    }

    @Bean
    public Validator rpcValidator()
    {
        return mock(Validator.class);
    }

    @Bean
    public TestService testService()
    {
        return spy(new TestServiceImpl());
    }

}
