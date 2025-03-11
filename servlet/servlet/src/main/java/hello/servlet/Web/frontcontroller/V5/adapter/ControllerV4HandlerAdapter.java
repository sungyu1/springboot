package hello.servlet.Web.frontcontroller.V5.adapter;

import hello.servlet.Web.frontcontroller.ModelView;
import hello.servlet.Web.frontcontroller.V4.ControllerV4;
import hello.servlet.Web.frontcontroller.V5.MyHandlerAdapter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ControllerV4HandlerAdapter implements MyHandlerAdapter {
    @Override
    public boolean supports(Object handler) {
        return (handler instanceof ControllerV4);
    }

    @Override
    public ModelView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException, IOException {
        ControllerV4 controller = (ControllerV4) handler;

        Map<String, String> paramMap = creatParmMap(request);
        HashMap<String , Object> model = new HashMap<>();

        String viewName = controller.pricess(paramMap, model);

        ModelView mv = new ModelView(viewName);
        mv.setModel(model);

        return mv;
    }
    private static Map<String, String> creatParmMap(HttpServletRequest request) {
        //      paramMap
        Map<String, String> paramMap = new HashMap<>();
//        request 에서 name값을 다가지고 온다.
        request.getParameterNames().asIterator()
                .forEachRemaining(paramName -> paramMap.put(paramName, request.getParameter(paramName)));
        return paramMap;
    }
}
