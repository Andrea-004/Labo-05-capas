package com.example.lab03capas.services.impl;

import com.example.lab03capas.common.mappers.SpecimenMapper;
import com.example.lab03capas.domain.dto.requests.CreateSpecimenRequest;
import com.example.lab03capas.domain.dto.response.specimen.SpecimenResponse;
import com.example.lab03capas.domain.entities.Specimen;
import com.example.lab03capas.exceptions.ResourceNotFoundException;
import com.example.lab03capas.repositories.SpecimenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpecimenServiceImplTest {

    @Mock
    private SpecimenRepository specimenRepository;

    @Mock
    private SpecimenMapper specimenMapper;

    @InjectMocks
    private SpecimenServiceImpl specimenService;

    private UUID specimenId;
    private Specimen specimenEntity;
    private SpecimenResponse specimenResponse;
    private CreateSpecimenRequest createRequest;

    @BeforeEach
    void setUp() {
        specimenId = UUID.randomUUID();

        createRequest = CreateSpecimenRequest.builder()
                .name("Bokoblin")
                .region("Akkala")
                .dangerLevel(3)
                .isFriendly(false)
                .build();

        specimenEntity = Specimen.builder()
                .id(specimenId)
                .name("Bokoblin")
                .region("Akkala")
                .dangerLevel(3)
                .isFriendly(false)
                .build();

        specimenResponse = SpecimenResponse.builder()
                .id(specimenId)
                .name("Bokoblin")
                .region("Akkala")
                .dangerLevel(3)
                .isFriendly(false)
                .build();
    }

    @Test
    void createSpecimen_shouldReturnSpecimenResponse_whenValidRequest() {
        when(specimenMapper.toEntityCreate(createRequest)).thenReturn(specimenEntity);
        when(specimenRepository.save(specimenEntity)).thenReturn(specimenEntity);
        when(specimenMapper.toDto(specimenEntity)).thenReturn(specimenResponse);

        SpecimenResponse result = specimenService.createSpecimen(createRequest);

        assertThat(result).isEqualTo(specimenResponse);
        assertThat(result.getName()).isEqualTo("Bokoblin");
        verify(specimenRepository, times(1)).save(specimenEntity);
    }

    @Test
    void getSpecimenById_shouldReturnSpecimenResponse_whenSpecimenExists() {
        when(specimenRepository.findById(specimenId)).thenReturn(Optional.of(specimenEntity));
        when(specimenMapper.toDto(specimenEntity)).thenReturn(specimenResponse);

        SpecimenResponse result = specimenService.getSpecimenById(specimenId);

        assertThat(result).isEqualTo(specimenResponse);
        assertThat(result.getId()).isEqualTo(specimenId);
        verify(specimenRepository, times(1)).findById(specimenId);
    }

    @Test
    void getSpecimenById_shouldThrowResourceNotFoundException_whenSpecimenNotFound() {
        when(specimenRepository.findById(specimenId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specimenService.getSpecimenById(specimenId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Specimen not found in Hyrule Records");

        verify(specimenRepository, times(1)).findById(specimenId);
    }

    @Test
    void deleteSpecimen_shouldDeleteAndReturnSpecimenResponse_whenSpecimenExists() {
        when(specimenRepository.findById(specimenId)).thenReturn(Optional.of(specimenEntity));
        when(specimenMapper.toDto(specimenEntity)).thenReturn(specimenResponse);

        SpecimenResponse result = specimenService.deleteSpecimen(specimenId);

        assertThat(result).isEqualTo(specimenResponse);
        verify(specimenRepository, times(1)).deleteById(specimenId);
    }
}