import com.jsoft.linkbuild.listenerAndServerLibrary.*;
import com.jsoft.linkbuild.utility.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TimerQuestion
{
   private Timer timer;
   private QuestionGame refQG;
   private String[] domande = {"Chi canta 'Rainbow' ? (Nome e Cognome)", "Chi ha scritto: 'Il nome della rosa' ? (Nome e Cognome)", "3*4-22 = ?",
                                "Il colore dei caschi dell'Onu", "Il festival del cinema della Palma d'Oro", "Bob, poetico cantautore americano",
                                "Clint, interprete di 'Per un pugno di dollari'", "Leggendario capo Apache", "Il vero cognome di Ollio",
                                "Lo era la Leggerezza dell'essere di Kundera", "Insetto luminoso", "La regione con Isernia"};
   private String[] risposte = {"elisa toffoli", "umberto eco", "-10", "blu", "cannes", "dylan", "eastwood", "geronimo", "hardy", "insostenibile","lucciola","molise"};
   private int[] tempoAssociato = {30,35,10,10,20,20,20,30,20,30,20,20};
   private int[] punti = {10,15,10,10,15,10,10,20,20,20,10,20};
   private int domandaAttuale;
   private boolean questionActive = false;
   
   public TimerQuestion(QuestionGame ref)
   {
       int delay = 1000; //milliseconds
       refQG = ref;

       ActionListener taskPerformer = new ActionListener()
       {
           public void actionPerformed(ActionEvent evt)
           {
               //Se c'e almeno un giocatore
               if(!(refQG.getGiocatore1().equals("") && refQG.getGiocatore2().equals("")) && !questionActive)
               {
                   //fai quello che devi fare per far vedere la domanda
                   domandaAttuale = (int)(domande.length*Math.random());
                   refQG.getDisplayDomanda().setText("* "+domande[domandaAttuale]);
                   refQG.getDisplayTempo().setText(""+tempoAssociato[domandaAttuale]);
                   refQG.getAppunti().append("NUOVA DOMANDA: NUOVA SFIDA!\n");
                   refQG.getAppunti().setCaretPosition(refQG.getAppunti().getDocument().getLength());
                   questionActive = true;
               }
               else if(!(refQG.getGiocatore1().equals("") && refQG.getGiocatore2().equals("")) && questionActive)
               {
                   int tempoAtt = Integer.parseInt(refQG.getDisplayTempo().getText());
                   if(tempoAtt <= 0)
                   {
                        try 
                        {
                            refQG.getAppunti().append("TEMPO SCADUTO!\n");
                            refQG.getAppunti().setCaretPosition(refQG.getAppunti().getDocument().getLength());
                            questionActive = false;
                            Thread.sleep(6000);
                        }
                        catch (InterruptedException ex)
                        {
                            Logger.getLogger(TimerQuestion.class.getName()).log(Level.SEVERE, null, ex);
                        }
                   }
                   else
                   {
                        refQG.getDisplayTempo().setText(""+(tempoAtt-1));
                   }
               }
               else if(refQG.getGiocatore1().equals("") && refQG.getGiocatore2().equals(""))
               {
                  refQG.getDisplayDomanda().setText("");
                  refQG.getDisplayTempo().setText("");
                  questionActive = false;                   
               }
           }
       };
       timer = new Timer(delay, taskPerformer); 
   }
   
   public void start()
   {
       this.questionActive = false;
       timer.start();
   }

   public void stop()
   {
       timer.stop();
   }
   
   public boolean isQuestionActive()
   {
       return this.questionActive;
   }
   
   public boolean isCurrentlyReply(String reply)
   {
       return this.risposte[this.domandaAttuale].equals(reply.toLowerCase());
   }
   
   public int getPuntiAttuali()
   {
       return this.punti[this.domandaAttuale]+Integer.parseInt(this.refQG.getDisplayTempo().getText());
   }
}
