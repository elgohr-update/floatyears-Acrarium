package com.faendir.acra.ui.navigation;

import com.faendir.acra.model.App;
import com.faendir.acra.security.SecurityUtils;
import com.faendir.acra.ui.annotation.RequiresAppPermission;
import com.faendir.acra.ui.view.base.ParametrizedBaseView;

/**
 * @author Lukas
 * @since 24.03.2018
 */
public abstract class SingleParametrizedViewProvider<T, V extends ParametrizedBaseView<T>> extends SingleViewProvider<V> {
    private String lastParameter;
    private T lastT;

    public SingleParametrizedViewProvider(Class<V> clazz) {
        super(clazz);
    }

    @Override
    protected boolean isValidParameter(String parameter) {
        T t = parseParameterInternal(parameter);
        RequiresAppPermission annotation;
        return isValidParameter(t) && ((annotation = getClazz().getAnnotation(RequiresAppPermission.class)) == null || SecurityUtils.hasPermission(toApp(t), annotation.value()));
    }

    @Override
    public V getView(String viewName) {
        V view = super.getView(viewName);
        if (view != null) {
            view.setParameterParser(e -> {
                String parameters = getParameters(e.getViewName().substring(e.getViewName().indexOf(getId())) + (e.getParameters().isEmpty() ?
                                                                                                                         "" :
                                                                                                                         MyNavigator.SEPARATOR_CHAR + e.getParameters()));
                return parameters != null ? parseParameterInternal(parameters) : null;
            });
        }
        return view;
    }

    @Override
    public final String getTitle(String parameter) {
        return getTitle(parseParameterInternal(parameter));
    }

    protected abstract String getTitle(T parameter);

    protected abstract boolean isValidParameter(T parameter);

    protected abstract T parseParameter(String parameter);

    protected abstract App toApp(T parameter);

    private T parseParameterInternal(String parameter) {
        if (parameter.equals(lastParameter)) {
            return lastT;
        }
        T t = parseParameter(parameter);
        lastParameter = parameter;
        lastT = t;
        return t;
    }
}
