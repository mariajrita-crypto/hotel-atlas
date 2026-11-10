package com.hotel;

import com.hotel.service.HotelData;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Arrays;

public class SolicitacoesController {
    @FXML private TableView<HotelData.ServiceRequest> tabelaSolicitacoes;

    @FXML public void initialize() {
        TableColumn<HotelData.ServiceRequest, Long> id = new TableColumn<>("ID");
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<HotelData.ServiceRequest, String> hospede = new TableColumn<>("Hóspede");
        hospede.setCellValueFactory(new PropertyValueFactory<>("guestName"));
        TableColumn<HotelData.ServiceRequest, Integer> quarto = new TableColumn<>("Quarto");
        quarto.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        TableColumn<HotelData.ServiceRequest, String> tipo = new TableColumn<>("Serviço / Chat");
        tipo.setCellValueFactory(new PropertyValueFactory<>("type"));
        TableColumn<HotelData.ServiceRequest, String> detalhes = new TableColumn<>("Solicitação");
        detalhes.setCellValueFactory(new PropertyValueFactory<>("details"));
        TableColumn<HotelData.ServiceRequest, String> status = new TableColumn<>("Status");
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        TableColumn<HotelData.ServiceRequest, String> data = new TableColumn<>("Enviada em");
        data.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        tabelaSolicitacoes.getColumns().setAll(id, hospede, quarto, tipo, detalhes, status, data);
        tabelaSolicitacoes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        recarregar();
    }

    @FXML private void recarregar() {
        if (tabelaSolicitacoes != null) {
            tabelaSolicitacoes.setItems(FXCollections.observableArrayList(
                    HotelData.getInstance().getRequests()));
            tabelaSolicitacoes.refresh();
        }
    }

    @FXML private void atender() {
        HotelData.ServiceRequest request = tabelaSolicitacoes.getSelectionModel().getSelectedItem();
        if (request == null) {
            aviso("Selecione uma solicitação na tabela para atender.");
            return;
        }
        ChoiceDialog<String> status = new ChoiceDialog<>(request.getStatus(),
                Arrays.asList("ABERTA", "EM_ATENDIMENTO", "CONCLUIDA", "CANCELADA"));
        status.setTitle("Atendimento Atlas");
        status.setHeaderText("Solicitação #" + request.getId());
        status.setContentText("Novo status:");
        status.showAndWait().ifPresent(novoStatus -> {
            TextInputDialog resposta = new TextInputDialog(request.getReply());
            resposta.setTitle("Resposta da recepção");
            resposta.setHeaderText("Responder a " + request.getGuestName());
            resposta.setContentText("Mensagem:");
            resposta.showAndWait().ifPresent(mensagem -> {
                try {
                    HotelData.getInstance().updateServiceRequest(request.getId(), novoStatus, mensagem);
                    recarregar();
                    aviso("Solicitação atualizada e resposta salva.");
                } catch (RuntimeException ex) {
                    erro("Não foi possível atualizar: " + ex.getMessage());
                }
            });
        });
    }

    private void aviso(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, mensagem, ButtonType.OK);
        alert.setHeaderText("Atlas Hotel");
        alert.showAndWait();
    }

    private void erro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR, mensagem, ButtonType.OK);
        alert.setHeaderText("Atlas Hotel");
        alert.showAndWait();
    }
}
