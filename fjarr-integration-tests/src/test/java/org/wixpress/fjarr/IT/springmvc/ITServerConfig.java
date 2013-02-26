package org.wixpress.fjarr.IT.springmvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.wixpress.fjarr.example.DataStructService;
import org.wixpress.fjarr.example.DataStructServiceImpl;
import org.wixpress.fjarr.example.InputDTO;
import org.wixpress.fjarr.json.FjarrJacksonModule;
import org.wixpress.fjarr.json.JsonRpcExtensionMethodExecutor;
import org.wixpress.fjarr.json.JsonRpcProtocol;
import org.wixpress.fjarr.json.extensionmethods.ServiceNameExtensionMethod;
import org.wixpress.fjarr.server.*;
import org.wixpress.fjarr.validation.SpringValidatorRpcEventHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerMapping;

/**
 * @author alex
 * @since 1/6/13 5:30 PM
 */

@Configuration
public class ITServerConfig
{
    @Bean
    public RpcServiceRegistry registry()
    {
        return new RpcServiceRegistry();
    }

    @Bean
    public RpcServiceRegistration testRegistration(final DataStructService dataStructService)
    {
        return new RpcServiceRegistration()
        {
            @Override
            public void registerServices()
            {
                registerEndpoint(DataStructService.class, dataStructService,
                        new JsonRpcExtensionMethodExecutor(
                                new ServiceNameExtensionMethod(DataStructService.class)
                        ));
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
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new FjarrJacksonModule());

        return new JsonRpcProtocol(
                mapper);
    }

    @Bean
    public RpcRequestLifecycleEventHandler eventHandler(Validator validator)
    {
        return new SpringValidatorRpcEventHandler(validator);
    }

    @Bean
    public Validator rpcValidator()
    {
        return new Validator()
        {
            @Override
            public boolean supports(Class<?> clazz)
            {
                return clazz.equals(InputDTO.class);
            }

            @Override
            public void validate(Object target, Errors errors)
            {
                if (target.getClass().equals(InputDTO.class))
                {
                    InputDTO t = (InputDTO) target;

                    if (t.getValue() == null)
                    {
                        errors.rejectValue("value", "null");
                    }
                }
            }
        };
    }

    @Bean
    public DataStructService dataStructService()
    {
        return new DataStructServiceImpl();
    }
}
