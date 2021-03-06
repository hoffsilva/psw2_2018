/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ceub.pswii.velha;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author rafael.soares
 */
@RestController
public class VelhaController {
    
    @Autowired
    private JogoRepository jogoRepository;
    
    @Autowired
    private JogadaRepository jogadaRepository;
    
    
    
    
    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/jogos", method = RequestMethod.POST)
    public Jogo criarJogo(@RequestBody Jogo jogo){
        jogo.setVez(jogo.getJogador1());
        return jogoRepository.save(jogo);
    }
    
    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/jogos", method = RequestMethod.GET)
    public Iterable<Jogo> listarJogos(@RequestParam(value = "finalizado", 
            defaultValue = "false") Boolean finalizado){
        if(finalizado == true){
            return jogoRepository.findByFinalizado(finalizado);
        } else {
            return jogoRepository.findAll();
        }
    }
    
    @CrossOrigin(origins = "*")
    @RequestMapping(method = RequestMethod.POST
                    , value = "/jogos/{id}/jogadas")
    public Jogada criarJogada(@PathVariable("id") Integer idJogo,
                              @RequestBody Jogada jogada){
        
        Jogo jogo = jogoRepository.findByIdentificador(idJogo);
        if(jogo != null){
            if(jogo.getJogadas() == null){
                jogo.setJogadas(new ArrayList<Jogada>());
            }
            
            if(!jogo.isFinalizado()){
                if(jogo.getVez() == null || 
                    jogo.getVez().equals("") ||
                    jogo.getVez().equals(jogada.getJogador())){
                
                        jogada = jogadaRepository.save(jogada);
                        jogo.getJogadas().add(jogada);

                        if(jogada.getJogador().equals(jogo.getJogador1())){
                            jogo.setVez(jogo.getJogador2());
                        } else {
                            jogo.setVez(jogo.getJogador1());
                        }
                        
                        jogoRepository.save(jogo);
                }
            }
            verificaFimJogo(jogo);
            
        }
        
        return null;
    }
    
    @CrossOrigin(origins = "*")
    @RequestMapping(method = RequestMethod.DELETE,
            value = "/jogos/{id}")
    public void deletarJogo(@PathVariable("id") Integer idJogo){
        jogoRepository.deleteById(idJogo);
    }
    
    @CrossOrigin(origins = "*")
    @RequestMapping(method = RequestMethod.GET,
            value = "/jogos/{id}")
    public Jogo buscarJogo(@PathVariable("id") Integer idJogo){
        return jogoRepository.findByIdentificador(idJogo);
    }

    private Jogo verificaFimJogo(Jogo jogo) {
        
        HashMap<String, String> tabuleiro = new HashMap<>();
        for (Jogada jogada : jogo.getJogadas()) {
            tabuleiro.put(jogada.getQuadrante(), jogada.getJogador());
        }
        final String q1 = "11";
        final String q2 = "12";
        final String q3 = "13";
        
        verificaPossibilidade(tabuleiro, "11", "12", "13", jogo); 
        verificaPossibilidade(tabuleiro, "21", "22", "23", jogo); 
        verificaPossibilidade(tabuleiro, "31", "32", "33", jogo); 
        verificaPossibilidade(tabuleiro, "11", "21", "31", jogo); 
        verificaPossibilidade(tabuleiro, "21", "22", "32", jogo); 
        verificaPossibilidade(tabuleiro, "13", "23", "33", jogo); 
        verificaPossibilidade(tabuleiro, "11", "22", "33", jogo); 
        verificaPossibilidade(tabuleiro, "13", "22", "31", jogo); 
        
        return jogo;
    }

    private void verificaPossibilidade(HashMap<String, String> tabuleiro, final String q1, final String q2, final String q3, Jogo jogo) {
        if(tabuleiro.get(q1) != null && tabuleiro.get(q1).equals(tabuleiro.get(q2))
                && tabuleiro.get(q2) != null && tabuleiro.get(q2).equals(tabuleiro.get(q3))){
            jogo.setFinalizado(true);
            jogo.setVencedor(tabuleiro.get(q1));
            jogoRepository.save(jogo);
        } 
        
    }
    
    
    
}
