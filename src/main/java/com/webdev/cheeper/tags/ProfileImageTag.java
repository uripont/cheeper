package com.webdev.cheeper.tags;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;
import jakarta.servlet.jsp.PageContext;
import java.io.IOException;
import com.webdev.cheeper.service.ImageService;

public class ProfileImageTag extends SimpleTagSupport {
    private String picture;
    private String cssClass;
    private String username;
    private boolean clickable = false;
    private final ImageService imageService = new ImageService();

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    @Override
    public void doTag() throws JspException, IOException {
        PageContext pageContext = (PageContext) getJspContext();
        String imagePath = imageService.getImagePath(picture);
        if (!imagePath.startsWith("http")) {
            imagePath = pageContext.getServletContext().getContextPath() + imagePath;
        }
        
        StringBuilder html = new StringBuilder();
        html.append("<img src=\"").append(imagePath).append("\"");
        
        if (cssClass != null && !cssClass.isEmpty()) {
            html.append(" class=\"").append(cssClass);
            if (clickable) {
                html.append(" clickable-profile");
            }
            html.append("\"");
        }

        html.append(" alt=\"Profile Picture\"");
        
        if (clickable && username != null && !username.isEmpty()) {
            html.append(" data-username=\"").append(username).append("\"");
        }
        
        html.append("/>");
        
        getJspContext().getOut().write(html.toString());
    }
}
