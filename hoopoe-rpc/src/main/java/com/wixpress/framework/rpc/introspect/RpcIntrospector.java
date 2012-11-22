package com.wixpress.framework.rpc.introspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.wixpress.framework.rpc.client.RpcProtocolClient;
import com.wixpress.framework.rpc.introspect.javadoc.DocumentationProvider;
import com.wixpress.framework.rpc.json.JsonRpcProtocolClient;
import com.wixpress.hoopoe.reflection.MethodReflector;
import com.wixpress.hoopoe.reflection.Reflectors;
import com.wixpress.hoopoe.reflection.genericTypes.GenericType;
import com.wixpress.hoopoe.reflection.reflectorsImpl.CachedReflectors;
import com.wixpress.hoopoe.url.Url;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Iterables.toArray;
import static com.google.common.collect.Lists.newArrayList;

/**
 * @author shaiyallin
 * @since 1/3/12
 */
public class RpcIntrospector implements ServletContextAware {

    private ServletContext servletContext;

    private Reflectors reflectors = new CachedReflectors();

    private final ExampleGenerator exampleGenerator;

    private final RpcProtocolClient protocol;
    private final DocumentationProvider documentationProvider;

    public RpcIntrospector(ExampleGenerator exampleGenerator,
                           DocumentationProvider documentationProvider,
                           ObjectMapper objectMapper) {
        this.exampleGenerator = exampleGenerator;
        this.documentationProvider = documentationProvider;


//         ObjectMapper mapper = new ObjectMapper()
//                .configure(SerializationFeature.INDENT_OUTPUT,true);
//        mapper.getSerializationConfig()


        protocol = new JsonRpcProtocolClient(objectMapper.copy().configure(SerializationFeature.INDENT_OUTPUT,true));
    }

    public List<SerivceDescription> describeServices(Map<String, Class<?>> services, final String servletPath) {

        return newArrayList(Iterables.transform(services.entrySet(), new Function<Map.Entry<String, Class<?>>, SerivceDescription>() {
            @Override
            public SerivceDescription apply(Map.Entry<String, Class<?>> entry) {
                return new SerivceDescription(
                        entry.getKey().replaceAll("/", "-"),
                        buildEndpointUrl(servletPath, entry.getKey()),
                        getMethods(entry.getValue()),
                        documentationProvider.getCommentForClass(entry.getValue())
                );
            }
        }));
    }

    private List<MethodDescription> getMethods(Class<?> serviceInterface) {
        return newArrayList(Iterables.transform(reflectors.reflect(serviceInterface).getMethods(), new Function<MethodReflector, MethodDescription>() {
            @Override
            public MethodDescription apply(MethodReflector reflector) {
                return describeMethod(reflector);
            }
        }));
    }

    private MethodDescription describeMethod(MethodReflector reflector) {
        return new MethodDescription(
                reflector.getName(),
                buildMethodExample(reflector.getName(), getParameters(reflector.getParameterTypes())),
                documentationProvider.getJavaDocForMethod(reflector)
        );
    }

    private String buildMethodExample(String name, Iterable<Object> paramExamples) {
        StringWriter sw = new StringWriter();
        try {
            protocol.writeRequest(sw, name, toArray(paramExamples, Object.class));
            return sw.toString();
        } catch (IOException e) {
            return "null; // failed creating example for method " + name;
        }
    }

    private Iterable<Object> getParameters(Iterable<GenericType> parameterTypes) {
        return Iterables.transform(parameterTypes, new Function<GenericType, Object>() {
            @Override
            public Object apply(GenericType type) {
                return exampleGenerator.generateExampleFor(type);
            }
        });
    }


    private Url buildEndpointUrl(String servletPath, String mappedUrl) {
        return new Url(servletContext.getContextPath()).withPart(servletPath).withPart(mappedUrl);
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

}
