package hello.servlet.Web.frontcontroller.V5;

import hello.servlet.Web.frontcontroller.ModelView;
import hello.servlet.Web.frontcontroller.MyView;

import hello.servlet.Web.frontcontroller.V3.controller.MemberFormControllerV3;
import hello.servlet.Web.frontcontroller.V3.controller.MemberListControllerV3;
import hello.servlet.Web.frontcontroller.V3.controller.MemberSaveControllerV3;

import hello.servlet.Web.frontcontroller.V4.controller.MemberFormControllerV4;
import hello.servlet.Web.frontcontroller.V4.controller.MemberListControllerV4;
import hello.servlet.Web.frontcontroller.V4.controller.MemberSaveControllerV4;
import hello.servlet.Web.frontcontroller.V5.adapter.ControllerV3HandlerAdapter;
import hello.servlet.Web.frontcontroller.V5.adapter.ControllerV4HandlerAdapter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name=" frontControllerServletV5",urlPatterns = "/front-controller/v5/*")
public class FrontControllerServletV5 extends HttpServlet {

    private final Map<String, Object>handMappingMap=new HashMap<>();
    private final List<MyHandlerAdapter>handlerAdapters=new ArrayList<>();

    public FrontControllerServletV5(){
//        메서드로 만듬
        initHandlerMappingMap();
//        메서드로 만듬
        initHandlerAdapters();
    }

    private void initHandlerMappingMap() {
        handMappingMap.put("/front-controller/v5/v3/members/new-form", new MemberFormControllerV3());
        handMappingMap.put("/front-controller/v5/v3/members/save", new MemberSaveControllerV3());
        handMappingMap.put("/front-controller/v5/v3/members", new MemberListControllerV3());

//        V4 추가
        handMappingMap.put("/front-controller/v5/v4/members/new-form", new MemberFormControllerV4());
        handMappingMap.put("/front-controller/v5/v4/members/save", new MemberSaveControllerV4());
        handMappingMap.put("/front-controller/v5/v4/members", new MemberListControllerV4());
    }

//   핸들러 추가
    private void initHandlerAdapters() {
        handlerAdapters.add(new ControllerV3HandlerAdapter());
        handlerAdapters.add(new ControllerV4HandlerAdapter());
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("FrontControllerServletV5.service 잘나옴");

//      메서드 만듬/ 핸들러를 찾음.
        Object handler = getHandler(request);

//      오류 있을시 페이지에 404 띄운다
        if(handler==null){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
//      메서드 만듬/ 핸들어 어댑터 찾음.
        MyHandlerAdapter adapter= getHandlerAdapt(handler);

        ModelView mv = adapter.handle(request, response, handler);

//      메소드로 변환
        Map<String, Object> model=new HashMap<>();// 새롭게 추가

        String viewName = mv.getViewName();
//      메소드로 변환
        MyView view = viewResolver(viewName);

        view.render(mv.getModel() ,request, response);
    }

    private MyHandlerAdapter getHandlerAdapt(Object handler) {
        for (MyHandlerAdapter adapter : handlerAdapters) {
            if(adapter.supports(handler)){
                return adapter;
            }
        }
//        아무것도 없을때
        throw new IllegalArgumentException("handler adapter를  찾을수 없습니다. "+handler);
    }

    private Object getHandler(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return handMappingMap.get(requestURI);
    }
    private static MyView viewResolver(String viewName) {

        return new MyView("/WEB-INF/views/" + viewName + ".jsp");
    }
}
