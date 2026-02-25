package com.davivienda.factoraje.service;

import org.springframework.stereotype.Service;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.resource.Emailv31;
import com.davivienda.factoraje.domain.dto.Emails.DestinatarioRequestDTO;
import com.davivienda.factoraje.domain.dto.Emails.HTMLVariablesDTO;
import com.davivienda.factoraje.exception.ResourceNotFoundException;
import com.mailjet.client.ClientOptions;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class MailjetEmailService {

    private static final Logger log = LoggerFactory.getLogger(AgreementService.class);
    private final ParameterService parameterService;
    private final TemplateService templateService;

    private static final String PARAM_KEY_API_KEY = "mailjet.api.key";
    private static final String PARAM_KEY_API_SECRET = "mailjet.api.secret";
    private static final String PARAM_KEY_EMAIL = "mailjet.email.sender";
    private static final String PARAM_KEY_EMAIL_NAME = "mailjet.email.sender.name";
    private static final String PARAM_KEY_EMAIL_SUBJECT = "mailjet.email.sender.subject";
    
    private String paramValueApiKey;
    private String paramValueApiSecret;
    private String paramValueEmailSender;
    private String paramValueEmailSenderName;
    private String paramValueEmailSenderSubject;

    private MailjetClient mailjetClient;

    @Autowired
    public MailjetEmailService(TemplateService templateService, ParameterService parameterService) {
        this.parameterService = parameterService;
        this.templateService = templateService;
    }

    private Optional<String> getParameter(String key) {
        try {
            String value = parameterService.getValueByKey(key);
            return Optional.ofNullable(value); 
        } catch (ResourceNotFoundException e) {
            log.warn("No se pudo obtener el parámetro '{}'.", key, e); 
            return Optional.empty();
        }
    }

    public void loadParameters() {

        paramValueApiKey = getParameter(PARAM_KEY_API_KEY).get();
        paramValueApiSecret = getParameter(PARAM_KEY_API_SECRET).get();
        paramValueEmailSender = getParameter(PARAM_KEY_EMAIL).get();
        paramValueEmailSenderName = getParameter(PARAM_KEY_EMAIL_NAME).get();
        paramValueEmailSenderSubject = getParameter(PARAM_KEY_EMAIL_SUBJECT).get();

        this.mailjetClient = new MailjetClient(
                paramValueApiKey,
                paramValueApiSecret,
                new ClientOptions("v3.1"));
    }

    private String obtenerHtmlPorTipo(int tipoHtml) {
        String fileName = "";
        switch (tipoHtml) {
            case 1:
                fileName = "authorize_disbursement.html";
                break;
            case 2:
                fileName = "supplier_notification.html";
                break;
            case 3:
                fileName = "approve_documents.html";
                break;
            case 4:
                fileName = "authorize_disbursement_v2.html";
                break;
            case 5:
                fileName = "payer_notification.html";
                break; 
            default:
                fileName = "invalid_html_type.html";
                break;
        }
        return templateService.loadHTMLTemplate(fileName);
    }

    private String reemplazarVariablesHtml(String html, HTMLVariablesDTO vars) {
    
        DecimalFormat df = new DecimalFormat("#,##0.00");     
        String montoFmt = df.format(vars.getMontoDesembolsar());
        String comission = df.format(vars.getComission());

        return html
                .replace("[sociedad]", vars.getNombreEmpresa())
                .replace("[ldc]", vars.getNumeroLineaCredito())
                .replace("[desembolsoTotal]", montoFmt)
                .replace("[nit]", vars.getNIT())
                .replace("[comisionTotal]", comission)
                .replace("[distrito]", vars.getDistrito())
                .replace("[municipio]", vars.getMunicipio())
                .replace("[departamento]", vars.getDepartamento())
                .replace("[numeroCuenta]", vars.getNumeroCuentaPagador());
    }

    public void sendEmail(List<DestinatarioRequestDTO> destinatarios, int tipoHtml, HTMLVariablesDTO variables)
            throws Exception {

        if (destinatarios == null || destinatarios.isEmpty()) {
            log.warn("La lista de destinatarios es nula o está vacía. No se enviarán correos.");
            return;
        }

        for (DestinatarioRequestDTO dest : destinatarios) {
            JSONArray toArray = new JSONArray();
            toArray.put(new JSONObject()
                    .put("Email", dest.getEmail())
                    .put("Name", dest.getName()));

            String htmlSeleccionado = obtenerHtmlPorTipo(tipoHtml);

            if(tipoHtml == 1 || tipoHtml == 4){
                htmlSeleccionado = reemplazarVariablesHtml(htmlSeleccionado, variables);
            }
            
            JSONObject mensaje = new JSONObject()
                    .put("From", new JSONObject()
                            .put("Email", paramValueEmailSender)
                            .put("Name", paramValueEmailSenderName))
                    .put("To", toArray) 
                    .put("Subject", paramValueEmailSenderSubject)
                    .put("HTMLPart", htmlSeleccionado);

            MailjetRequest request = new MailjetRequest(Emailv31.resource)
                    .property(Emailv31.MESSAGES, new JSONArray().put(mensaje));

            MailjetResponse response = mailjetClient.post(request);
            System.out.println("Enviando email ..."+" | Status: " + response.getStatus());
        }
    }
}
