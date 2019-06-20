package at.qe.sepm.skeleton.ui.renderer;

import java.io.IOException;
import java.util.Iterator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIMessages;
import javax.faces.context.FacesContext;
import javax.faces.render.FacesRenderer;
import javax.faces.context.ResponseWriter;

import com.sun.faces.renderkit.Attribute;
import com.sun.faces.renderkit.AttributeManager;
import com.sun.faces.renderkit.RenderKitUtils;
import com.sun.faces.renderkit.html_basic.MessagesRenderer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage.Severity;

/**
 * Component for twitter bootstrap alerts.
 * Overrides default JSF Message renderer with Bootstrap alert design.
 *
 * @author vlcekmi3 (https://gist.github.com/vlcekmi3/4151211)
 * found on: https://memorynotfound.com/jsf-bootstrap-alert-closable-info-warning-and-error-styled-messages/
 */
@FacesRenderer(componentFamily="javax.faces.Messages", rendererType="javax.faces.Messages")
public class BootstrapMessagesRenderer extends MessagesRenderer {

    private static final Attribute[] ATTRIBUTES =
            AttributeManager.getAttributes(AttributeManager.Key.MESSAGESMESSAGES);

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        super.encodeBegin(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {

        rendererParamsNotNull(context, component);
        if (!shouldEncode(component)) return;

        boolean mustRender = shouldWriteIdAttribute(component);
        UIMessages messages = (UIMessages) component;
        ResponseWriter writer = context.getResponseWriter();

        String clientId = ((UIMessages) component).getFor();
        if (clientId == null && messages.isGlobalOnly()) {
            clientId = "";
        }

        Iterator messageIt = getMessageIter(context, clientId, component);
        if (!messageIt.hasNext()) {
            if (mustRender) {
                if ("javax_faces_developmentstage_messages".equals(component.getId())) {
                    return;
                }
                writer.startElement("div", component);
                writeIdAttributeIfNecessary(context, writer, component);
                writer.endElement("div");
            }
            return;
        }

        writeIdAttributeIfNecessary(context, writer, component);
        RenderKitUtils.renderPassThruAttributes(context, writer, component, ATTRIBUTES);

        Map<Severity, List<FacesMessage>> msgs = new HashMap<Severity, List<FacesMessage>>();
        msgs.put(FacesMessage.SEVERITY_INFO, new ArrayList<FacesMessage>());
        msgs.put(FacesMessage.SEVERITY_WARN, new ArrayList<FacesMessage>());
        msgs.put(FacesMessage.SEVERITY_ERROR, new ArrayList<FacesMessage>());
        msgs.put(FacesMessage.SEVERITY_FATAL, new ArrayList<FacesMessage>());

        while (messageIt.hasNext()) {
            FacesMessage curMessage = (FacesMessage) messageIt.next();
            if (curMessage.isRendered() && !messages.isRedisplay()) {
                continue;
            }
            msgs.get(curMessage.getSeverity()).add(curMessage);
        }

        List<FacesMessage> severityMessages = msgs.get(FacesMessage.SEVERITY_FATAL);
        if (!severityMessages.isEmpty()){
            encodeSeverityMessages(context, messages, FacesMessage.SEVERITY_FATAL, severityMessages);
        }

        severityMessages = msgs.get(FacesMessage.SEVERITY_ERROR);
        if (!severityMessages.isEmpty()){
            encodeSeverityMessages(context, messages, FacesMessage.SEVERITY_ERROR, severityMessages);
        }

        severityMessages = msgs.get(FacesMessage.SEVERITY_WARN);
        if (!severityMessages.isEmpty()){
            encodeSeverityMessages(context, messages, FacesMessage.SEVERITY_WARN, severityMessages);
        }

        severityMessages = msgs.get(FacesMessage.SEVERITY_INFO);
        if (!severityMessages.isEmpty()){
            encodeSeverityMessages(context, messages, FacesMessage.SEVERITY_INFO, severityMessages);
        }
    }

    private void encodeSeverityMessages(FacesContext facesContext, UIMessages uiMessages,
                                        Severity severity, List<FacesMessage> messages) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();
        String alertSeverityClass = "";

        if (FacesMessage.SEVERITY_INFO.equals(severity)) {
            alertSeverityClass = "alert-info";
        } else if (FacesMessage.SEVERITY_WARN.equals(severity)) {
            alertSeverityClass = "alert-warning";
        } else if (FacesMessage.SEVERITY_ERROR.equals(severity)) {
            alertSeverityClass = "alert-error";
        } else if (FacesMessage.SEVERITY_FATAL.equals(severity)) {
            alertSeverityClass = "alert-fatal";
        }


        writer.startElement("div", uiMessages);
        writer.writeAttribute("class", "alert-container", "alert-container");
        writer.startElement("div", uiMessages);
        writer.writeAttribute("class", "alert-cell", "alert-cell");


        for (FacesMessage msg : messages){
            String summary = msg.getSummary() != null ? msg.getSummary() : "";
            String detail = msg.getDetail() != null ? msg.getDetail() : summary;

            writer.startElement("div", null); //start individual container div
            writer.writeAttribute("class", "alert " + alertSeverityClass + " add-margin" + " fade show", "alert " + alertSeverityClass + " add-margin" + " fade show");
            writer.writeAttribute("role", "alert", "alert");

            writer.startElement("div", null); //start icon-div
            writer.writeAttribute("class", "alert-icon", "alert-icon");
            switch (alertSeverityClass) {

                case ("alert-info"):
                    writer.write("<i data-feather=\"check\"></i>");
                    break;
                case ("alert-warning"):
                    writer.write("<i data-feather=\"alert-circle\"></i>");
                    System.out.println("warning");
                    break;
                case ("alert-error"):
                    writer.write("<i data-feather=\"slash\"></i>");
                    System.out.println("error");
                    break;
                case ("alert-fatal"):
                    writer.write("<i data-feather=\"zap\"></i>");
                    System.out.println("fatal");
                    break;
            }
            writer.endElement("div"); //end icon-div

            writer.startElement("div", null); //start content-div
            writer.writeAttribute("class", "alert-content", "alert-content");
            if (uiMessages.isShowSummary()) {
                writer.startElement("p", uiMessages); //start paragraph for summary
                writer.writeAttribute("class", "alert-summary", "alert-summary");
                writer.writeText(summary, uiMessages, null);
                writer.endElement("p"); //end paragraph for summary
            }

            if (uiMessages.isShowDetail()) {
                writer.startElement("p", uiMessages);
                writer.writeAttribute("class", "alert-text", "alert-text");
                writer.writeText(" " + detail, null);
                writer.endElement("p");
            }
            writer.endElement("div"); //end content-div

            writer.startElement("div", null);
            writer.writeAttribute("class", "alert-close", "alert-close");
            writer.write("<button type=\"button\" class=\"close alert-close\" id=\"close-modal-button\" data-dismiss=\"alert\" aria-label=\"Close\">\n" +
                    "    <span aria-hidden=\"true\">&times;</span>\n" +
                    "  </button>");
            writer.endElement("div");
            writer.endElement("div"); //end  individual container div
            msg.rendered();
        }

        writer.endElement("div"); //end of cell
        writer.endElement("div"); //end of alert-container

        writer.write("    <script type=\"text/javascript\">\n" +
                "            window.setTimeout(function () {\n" +
                "                $(\"#close-modal-button\").click();\n" +
                "            }, 5000);\n" +
                "    </script>");
    }
}
