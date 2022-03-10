package itmo.gorshkov.service;

import itmo.gorshkov.config.FilterConfiguration;
import itmo.gorshkov.entity.MusicBand;
import itmo.gorshkov.repository.MusicBandRepository;
import itmo.gorshkov.repository.MusicBandRepositoryImpl;
import itmo.gorshkov.util.CountByResult;

import javax.ejb.Stateless;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class MusicBandServiceBean implements MusicBandService {
    private final MusicBandRepository musicBandRepository;

    public MusicBandServiceBean() {
        this.musicBandRepository = new MusicBandRepositoryImpl();
    }

    @Override
    public List<MusicBand> findAll(FilterConfiguration filterConfiguration) {
        return musicBandRepository.findAll(filterConfiguration);
    }

    @Override
    public MusicBand save(MusicBand musicBand) {
        if (musicBand.getId() == null || musicBandRepository.findById(musicBand.getId()) == null) {
            processCreationDate(musicBand);
            musicBandRepository.save(musicBand);
            return musicBand;
        } else {
            throw new NotFoundException(Response.status(HttpServletResponse.SC_NOT_FOUND).entity("is shouldn't present").build());
        }
    }

    @Override
    public MusicBand update(MusicBand musicBand) {
        isIdExist(musicBand.getId());

        return musicBandRepository.update(musicBand);
    }

    @Override
    public MusicBand findById(Integer id) {
        return musicBandRepository.findById(id);
    }

    @Override
    public void delete(Integer id) {
        isIdExist(id);

        musicBandRepository.delete(id);
    }

    @Override
    public List<CountByResult> countByNumberOfParticipants() {
        return musicBandRepository.countByNumberOfParticipants().stream()
                .map(row -> new CountByResult((int) row[0], (java.math.BigInteger) row[1]))
                .collect(Collectors.toList());
    }

    private void processCreationDate(MusicBand musicBand) {
        if (musicBand.getCreationDate() == null) {
            musicBand.setCreationDate(LocalDate.now());
        }
    }

    private void isIdExist(Integer id) {
        if (id == null || musicBandRepository.findById(id) == null) {
            throw new NotFoundException(Response.status(HttpServletResponse.SC_NOT_FOUND).entity(id + " not found").build());
        }
    }
}