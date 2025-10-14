package br.com.muita_conta.service.email;

import br.com.muita_conta.config.RabbitMQConfig;
import br.com.muita_conta.exception.EmailApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final RabbitTemplate rabbitTemplate;

    public void enviarCodigoDeAtivacaoDeConta(String para, String nome, String codigo) {

        var emailMessage = new EmailMessageDTO(
            para,
            nome,
            codigo
        );

        try {
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                emailMessage
            );
        } catch (Exception e) {
            log.error("Falha ao publicar mensagem de e-mail para {}: {}", para, e.getMessage());
            throw new EmailApiException("Falha ao enviar o e-mail de verificação.", e);
        }
    }
}