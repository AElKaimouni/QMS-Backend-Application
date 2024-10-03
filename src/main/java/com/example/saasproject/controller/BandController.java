package com.example.saasproject.controller;

import com.example.saasproject.model.Band;
import com.example.saasproject.repository.BandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/bandGroupApi")
public class BandController {

    @Autowired
    private BandRepository bandRepository;

    // Get all bands
    @GetMapping
    public List<Band> getAllBands() {
        return bandRepository.findAll();
    }

    // Get a band by ID
    @GetMapping("/{id}")
    public ResponseEntity<Band> getBandById(@PathVariable Long id) {
        Optional<Band> band = bandRepository.findById(id);
        return band.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Create a new band
    @PostMapping
    public Band createBand(@RequestBody Band band) {
        return bandRepository.save(band);
    }

    // Update an existing band
    @PutMapping("/{id}")
    public ResponseEntity<Band> updateBand(@PathVariable Long id, @RequestBody Band bandDetails) {
        Optional<Band> optionalBand = bandRepository.findById(id);

        if (optionalBand.isPresent()) {
            Band band = optionalBand.get();
            band.setName(bandDetails.getName());
            band.setGenre(bandDetails.getGenre());
            Band updatedBand = bandRepository.save(band);
            return ResponseEntity.ok(updatedBand);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete a band
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBand(@PathVariable Long id) {
        Optional<Band> band = bandRepository.findById(id);
        if (band.isPresent()) {
            bandRepository.delete(band.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
