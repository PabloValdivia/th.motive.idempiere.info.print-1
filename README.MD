# DATA: 21/06/2019

## Patch core iDempiere

**Classe:** *org.idempiere.info.print.osgi.component.DefaultInfoPanelPrintMange*
**Riga:** *211*

......
`Collection<KeyNamePair> selectedKeypairs = infoPanel.**getSelectedKeyForPrint** (isPrintAll);`
.....
Il metodo è stato aggiunto come patch, nella classe InfoPanel.java. 
[patch di riferimento: IDEMPIERE-3784]
La patch non è stata creata a livello core_iDempiere, ma all'interno del fragm_patch **idIta.modification.org.adempiere.ui.zk** 

A livello ambiente Eclipse, risulta un errore di compilazione sulla classe; ma facendo compilazione a livello Maven, il compilato nella sotto_cartella_plug-in /target/classes è corretto. Tenere conto di questo. 

Il plug-in jar in ambiente iDempiere-Server a tutti gli effetti funziona, non da errori;  lanciando iDempiere in ambiente Eclipse:

-  al lancio del RUN, procedere ugualmente anche se c'è errore nella classe menzionata prima..... 
- e se una volta dentro iDempiere la funzionalità del plug-in desse un errore, basta cancellare TUTTE le "eventuali" classi compilate all'interno della sotto_cartella_plug-in /bin (queste con non servono più dalla 6.2.....) e riavviare il servizio iDempiere.