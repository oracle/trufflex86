package org.graalvm.vm.x86.test.runner;

import org.junit.Test;

public class BenchmarksGame {
    @Test
    public void fasta_cint() throws Exception {
        String stdout = ">ONE Homo sapiens alu\n" +
                        "GGCCGGGCGCGGTGGCTCACGCCTGTAATCCCAGCACTTTGGGAGGCCGAGGCGGGCGGA\n" +
                        "TCACCTGAGGTCAGGAGTTCGAGACCAGCCTGGCCAACATGGTGAAACCCCGTCTCTACT\n" +
                        "AAAAATACAAAAATTAGCCGGGCGTGGTGGCGCGCGCCTGTAATCCCAGCTACTCGGGAG\n" +
                        "GCTGAGGCAGGAGAATCGCTTGAACCCGGGAGGCGGAGGTTGCAGTGAGCCGAGATCGCG\n" +
                        "CCACTGCACTCCAGCCTGGGCGACAGAGCGAGACTCCGTCTCAAAAAGGCCGGGCGCGGT\n" +
                        "GGCTCACGCCTGTAATCCCAGCACTTTGGGAGGCCGAGGCGGGCGGATCACCTGAGGTCA\n" +
                        "GGAGTTCGAGACCAGCCTGGCCAACATGGTGAAACCCCGTCTCTACTAAAAATACAAAAA\n" +
                        "TTAGCCGGGCGTGGTGGCGCGCGCCTGTAATCCCAGCTACTCGGGAGGCTGAGGCAGGAG\n" +
                        "AATCGCTTGAACCCGGGAGGCGGAGGTTGCAGTGAGCCGAGATCGCGCCACTGCACTCCA\n" +
                        "GCCTGGGCGACAGAGCGAGACTCCGTCTCAAAAAGGCCGGGCGCGGTGGCTCACGCCTGT\n" +
                        "AATCCCAGCACTTTGGGAGGCCGAGGCGGGCGGATCACCTGAGGTCAGGAGTTCGAGACC\n" +
                        "AGCCTGGCCAACATGGTGAAACCCCGTCTCTACTAAAAATACAAAAATTAGCCGGGCGTG\n" +
                        "GTGGCGCGCGCCTGTAATCCCAGCTACTCGGGAGGCTGAGGCAGGAGAATCGCTTGAACC\n" +
                        "CGGGAGGCGGAGGTTGCAGTGAGCCGAGATCGCGCCACTGCACTCCAGCCTGGGCGACAG\n" +
                        "AGCGAGACTCCGTCTCAAAAAGGCCGGGCGCGGTGGCTCACGCCTGTAATCCCAGCACTT\n" +
                        "TGGGAGGCCGAGGCGGGCGGATCACCTGAGGTCAGGAGTTCGAGACCAGCCTGGCCAACA\n" +
                        "TGGTGAAACCCCGTCTCTACTAAAAATACAAAAATTAGCCGGGCGTGGTGGCGCGCGCCT\n" +
                        "GTAATCCCAGCTACTCGGGAGGCTGAGGCAGGAGAATCGCTTGAACCCGGGAGGCGGAGG\n" +
                        "TTGCAGTGAGCCGAGATCGCGCCACTGCACTCCAGCCTGGGCGACAGAGCGAGACTCCGT\n" +
                        "CTCAAAAAGGCCGGGCGCGGTGGCTCACGCCTGTAATCCCAGCACTTTGGGAGGCCGAGG\n" +
                        "CGGGCGGATCACCTGAGGTCAGGAGTTCGAGACCAGCCTGGCCAACATGGTGAAACCCCG\n" +
                        "TCTCTACTAAAAATACAAAAATTAGCCGGGCGTGGTGGCGCGCGCCTGTAATCCCAGCTA\n" +
                        "CTCGGGAGGCTGAGGCAGGAGAATCGCTTGAACCCGGGAGGCGGAGGTTGCAGTGAGCCG\n" +
                        "AGATCGCGCCACTGCACTCCAGCCTGGGCGACAGAGCGAGACTCCGTCTCAAAAAGGCCG\n" +
                        "GGCGCGGTGGCTCACGCCTGTAATCCCAGCACTTTGGGAGGCCGAGGCGGGCGGATCACC\n" +
                        "TGAGGTCAGGAGTTCGAGACCAGCCTGGCCAACATGGTGAAACCCCGTCTCTACTAAAAA\n" +
                        "TACAAAAATTAGCCGGGCGTGGTGGCGCGCGCCTGTAATCCCAGCTACTCGGGAGGCTGA\n" +
                        "GGCAGGAGAATCGCTTGAACCCGGGAGGCGGAGGTTGCAGTGAGCCGAGATCGCGCCACT\n" +
                        "GCACTCCAGCCTGGGCGACAGAGCGAGACTCCGTCTCAAAAAGGCCGGGCGCGGTGGCTC\n" +
                        "ACGCCTGTAATCCCAGCACTTTGGGAGGCCGAGGCGGGCGGATCACCTGAGGTCAGGAGT\n" +
                        "TCGAGACCAGCCTGGCCAACATGGTGAAACCCCGTCTCTACTAAAAATACAAAAATTAGC\n" +
                        "CGGGCGTGGTGGCGCGCGCCTGTAATCCCAGCTACTCGGGAGGCTGAGGCAGGAGAATCG\n" +
                        "CTTGAACCCGGGAGGCGGAGGTTGCAGTGAGCCGAGATCGCGCCACTGCACTCCAGCCTG\n" +
                        "GGCGACAGAGCGAGACTCCG\n" +
                        ">TWO IUB ambiguity codes\n" +
                        "cttBtatcatatgctaKggNcataaaSatgtaaaDcDRtBggDtctttataattcBgtcg\n" +
                        "tactDtDagcctatttSVHtHttKtgtHMaSattgWaHKHttttagacatWatgtRgaaa\n" +
                        "NtactMcSMtYtcMgRtacttctWBacgaaatatagScDtttgaagacacatagtVgYgt\n" +
                        "cattHWtMMWcStgttaggKtSgaYaaccWStcgBttgcgaMttBYatcWtgacaYcaga\n" +
                        "gtaBDtRacttttcWatMttDBcatWtatcttactaBgaYtcttgttttttttYaaScYa\n" +
                        "HgtgttNtSatcMtcVaaaStccRcctDaataataStcYtRDSaMtDttgttSagtRRca\n" +
                        "tttHatSttMtWgtcgtatSSagactYaaattcaMtWatttaSgYttaRgKaRtccactt\n" +
                        "tattRggaMcDaWaWagttttgacatgttctacaaaRaatataataaMttcgDacgaSSt\n" +
                        "acaStYRctVaNMtMgtaggcKatcttttattaaaaagVWaHKYagtttttatttaacct\n" +
                        "tacgtVtcVaattVMBcttaMtttaStgacttagattWWacVtgWYagWVRctDattBYt\n" +
                        "gtttaagaagattattgacVatMaacattVctgtBSgaVtgWWggaKHaatKWcBScSWa\n" +
                        "accRVacacaaactaccScattRatatKVtactatatttHttaagtttSKtRtacaaagt\n" +
                        "RDttcaaaaWgcacatWaDgtDKacgaacaattacaRNWaatHtttStgttattaaMtgt\n" +
                        "tgDcgtMgcatBtgcttcgcgaDWgagctgcgaggggVtaaScNatttacttaatgacag\n" +
                        "cccccacatYScaMgtaggtYaNgttctgaMaacNaMRaacaaacaKctacatagYWctg\n" +
                        "ttWaaataaaataRattagHacacaagcgKatacBttRttaagtatttccgatctHSaat\n" +
                        "actcNttMaagtattMtgRtgaMgcataatHcMtaBSaRattagttgatHtMttaaKagg\n" +
                        "YtaaBataSaVatactWtataVWgKgttaaaacagtgcgRatatacatVtHRtVYataSa\n" +
                        "KtWaStVcNKHKttactatccctcatgWHatWaRcttactaggatctataDtDHBttata\n" +
                        "aaaHgtacVtagaYttYaKcctattcttcttaataNDaaggaaaDYgcggctaaWSctBa\n" +
                        "aNtgctggMBaKctaMVKagBaactaWaDaMaccYVtNtaHtVWtKgRtcaaNtYaNacg\n" +
                        "gtttNattgVtttctgtBaWgtaattcaagtcaVWtactNggattctttaYtaaagccgc\n" +
                        "tcttagHVggaYtgtNcDaVagctctctKgacgtatagYcctRYHDtgBattDaaDgccK\n" +
                        "tcHaaStttMcctagtattgcRgWBaVatHaaaataYtgtttagMDMRtaataaggatMt\n" +
                        "ttctWgtNtgtgaaaaMaatatRtttMtDgHHtgtcattttcWattRSHcVagaagtacg\n" +
                        "ggtaKVattKYagactNaatgtttgKMMgYNtcccgSKttctaStatatNVataYHgtNa\n" +
                        "BKRgNacaactgatttcctttaNcgatttctctataScaHtataRagtcRVttacDSDtt\n" +
                        "aRtSatacHgtSKacYagttMHtWataggatgactNtatSaNctataVtttRNKtgRacc\n" +
                        "tttYtatgttactttttcctttaaacatacaHactMacacggtWataMtBVacRaSaatc\n" +
                        "cgtaBVttccagccBcttaRKtgtgcctttttRtgtcagcRttKtaaacKtaaatctcac\n" +
                        "aattgcaNtSBaaccgggttattaaBcKatDagttactcttcattVtttHaaggctKKga\n" +
                        "tacatcBggScagtVcacattttgaHaDSgHatRMaHWggtatatRgccDttcgtatcga\n" +
                        "aacaHtaagttaRatgaVacttagattVKtaaYttaaatcaNatccRttRRaMScNaaaD\n" +
                        "gttVHWgtcHaaHgacVaWtgttScactaagSgttatcttagggDtaccagWattWtRtg\n" +
                        "ttHWHacgattBtgVcaYatcggttgagKcWtKKcaVtgaYgWctgYggVctgtHgaNcV\n" +
                        "taBtWaaYatcDRaaRtSctgaHaYRttagatMatgcatttNattaDttaattgttctaa\n" +
                        "ccctcccctagaWBtttHtBccttagaVaatMcBHagaVcWcagBVttcBtaYMccagat\n" +
                        "gaaaaHctctaacgttagNWRtcggattNatcRaNHttcagtKttttgWatWttcSaNgg\n" +
                        "gaWtactKKMaacatKatacNattgctWtatctaVgagctatgtRaHtYcWcttagccaa\n" +
                        "tYttWttaWSSttaHcaaaaagVacVgtaVaRMgattaVcDactttcHHggHRtgNcctt\n" +
                        "tYatcatKgctcctctatVcaaaaKaaaagtatatctgMtWtaaaacaStttMtcgactt\n" +
                        "taSatcgDataaactaaacaagtaaVctaggaSccaatMVtaaSKNVattttgHccatca\n" +
                        "cBVctgcaVatVttRtactgtVcaattHgtaaattaaattttYtatattaaRSgYtgBag\n" +
                        "aHSBDgtagcacRHtYcBgtcacttacactaYcgctWtattgSHtSatcataaatataHt\n" +
                        "cgtYaaMNgBaatttaRgaMaatatttBtttaaaHHKaatctgatWatYaacttMctctt\n" +
                        "ttVctagctDaaagtaVaKaKRtaacBgtatccaaccactHHaagaagaaggaNaaatBW\n" +
                        "attccgStaMSaMatBttgcatgRSacgttVVtaaDMtcSgVatWcaSatcttttVatag\n" +
                        "ttactttacgatcaccNtaDVgSRcgVcgtgaacgaNtaNatatagtHtMgtHcMtagaa\n" +
                        "attBgtataRaaaacaYKgtRccYtatgaagtaataKgtaaMttgaaRVatgcagaKStc\n" +
                        "tHNaaatctBBtcttaYaBWHgtVtgacagcaRcataWctcaBcYacYgatDgtDHccta\n" +
                        ">THREE Homo sapiens frequency\n" +
                        "aacacttcaccaggtatcgtgaaggctcaagattacccagagaacctttgcaatataaga\n" +
                        "atatgtatgcagcattaccctaagtaattatattctttttctgactcaaagtgacaagcc\n" +
                        "ctagtgtatattaaatcggtatatttgggaaattcctcaaactatcctaatcaggtagcc\n" +
                        "atgaaagtgatcaaaaaagttcgtacttataccatacatgaattctggccaagtaaaaaa\n" +
                        "tagattgcgcaaaattcgtaccttaagtctctcgccaagatattaggatcctattactca\n" +
                        "tatcgtgtttttctttattgccgccatccccggagtatctcacccatccttctcttaaag\n" +
                        "gcctaatattacctatgcaaataaacatatattgttgaaaattgagaacctgatcgtgat\n" +
                        "tcttatgtgtaccatatgtatagtaatcacgcgactatatagtgctttagtatcgcccgt\n" +
                        "gggtgagtgaatattctgggctagcgtgagatagtttcttgtcctaatatttttcagatc\n" +
                        "gaatagcttctatttttgtgtttattgacatatgtcgaaactccttactcagtgaaagtc\n" +
                        "atgaccagatccacgaacaatcttcggaatcagtctcgttttacggcggaatcttgagtc\n" +
                        "taacttatatcccgtcgcttactttctaacaccccttatgtatttttaaaattacgttta\n" +
                        "ttcgaacgtacttggcggaagcgttattttttgaagtaagttacattgggcagactcttg\n" +
                        "acattttcgatacgactttctttcatccatcacaggactcgttcgtattgatatcagaag\n" +
                        "ctcgtgatgattagttgtcttctttaccaatactttgaggcctattctgcgaaatttttg\n" +
                        "ttgccctgcgaacttcacataccaaggaacacctcgcaacatgccttcatatccatcgtt\n" +
                        "cattgtaattcttacacaatgaatcctaagtaattacatccctgcgtaaaagatggtagg\n" +
                        "ggcactgaggatatattaccaagcatttagttatgagtaatcagcaatgtttcttgtatt\n" +
                        "aagttctctaaaatagttacatcgtaatgttatctcgggttccgcgaataaacgagatag\n" +
                        "attcattatatatggccctaagcaaaaacctcctcgtattctgttggtaattagaatcac\n" +
                        "acaatacgggttgagatattaattatttgtagtacgaagagatataaaaagatgaacaat\n" +
                        "tactcaagtcaagatgtatacgggatttataataaaaatcgggtagagatctgctttgca\n" +
                        "attcagacgtgccactaaatcgtaatatgtcgcgttacatcagaaagggtaactattatt\n" +
                        "aattaataaagggcttaatcactacatattagatcttatccgatagtcttatctattcgt\n" +
                        "tgtatttttaagcggttctaattcagtcattatatcagtgctccgagttctttattattg\n" +
                        "ttttaaggatgacaaaatgcctcttgttataacgctgggagaagcagactaagagtcgga\n" +
                        "gcagttggtagaatgaggctgcaaaagacggtctcgacgaatggacagactttactaaac\n" +
                        "caatgaaagacagaagtagagcaaagtctgaagtggtatcagcttaattatgacaaccct\n" +
                        "taatacttccctttcgccgaatactggcgtggaaaggttttaaaagtcgaagtagttaga\n" +
                        "ggcatctctcgctcataaataggtagactactcgcaatccaatgtgactatgtaatactg\n" +
                        "ggaacatcagtccgcgatgcagcgtgtttatcaaccgtccccactcgcctggggagacat\n" +
                        "gagaccacccccgtggggattattagtccgcagtaatcgactcttgacaatccttttcga\n" +
                        "ttatgtcatagcaatttacgacagttcagcgaagtgactactcggcgaaatggtattact\n" +
                        "aaagcattcgaacccacatgaatgtgattcttggcaatttctaatccactaaagcttttc\n" +
                        "cgttgaatctggttgtagatatttatataagttcactaattaagatcacggtagtatatt\n" +
                        "gatagtgatgtctttgcaagaggttggccgaggaatttacggattctctattgatacaat\n" +
                        "ttgtctggcttataactcttaaggctgaaccaggcgtttttagacgacttgatcagctgt\n" +
                        "tagaatggtttggactccctctttcatgtcagtaacatttcagccgttattgttacgata\n" +
                        "tgcttgaacaatattgatctaccacacacccatagtatattttataggtcatgctgttac\n" +
                        "ctacgagcatggtattccacttcccattcaatgagtattcaacatcactagcctcagaga\n" +
                        "tgatgacccacctctaataacgtcacgttgcggccatgtgaaacctgaacttgagtagac\n" +
                        "gatatcaagcgctttaaattgcatataacatttgagggtaaagctaagcggatgctttat\n" +
                        "ataatcaatactcaataataagatttgattgcattttagagttatgacacgacatagttc\n" +
                        "actaacgagttactattcccagatctagactgaagtactgatcgagacgatccttacgtc\n" +
                        "gatgatcgttagttatcgacttaggtcgggtctctagcggtattggtacttaaccggaca\n" +
                        "ctatactaataacccatgatcaaagcataacagaatacagacgataatttcgccaacata\n" +
                        "tatgtacagaccccaagcatgagaagctcattgaaagctatcattgaagtcccgctcaca\n" +
                        "atgtgtcttttccagacggtttaactggttcccgggagtcctggagtttcgacttacata\n" +
                        "aatggaaacaatgtattttgctaatttatctatagcgtcatttggaccaatacagaatat\n" +
                        "tatgttgcctagtaatccactataacccgcaagtgctgatagaaaatttttagacgattt\n" +
                        "ataaatgccccaagtatccctcccgtgaatcctccgttatactaattagtattcgttcat\n" +
                        "acgtataccgcgcatatatgaacatttggcgataaggcgcgtgaattgttacgtgacaga\n" +
                        "gatagcagtttcttgtgatatggttaacagacgtacatgaagggaaactttatatctata\n" +
                        "gtgatgcttccgtagaaataccgccactggtctgccaatgatgaagtatgtagctttagg\n" +
                        "tttgtactatgaggctttcgtttgtttgcagagtataacagttgcgagtgaaaaaccgac\n" +
                        "gaatttatactaatacgctttcactattggctacaaaatagggaagagtttcaatcatga\n" +
                        "gagggagtatatggatgctttgtagctaaaggtagaacgtatgtatatgctgccgttcat\n" +
                        "tcttgaaagatacataagcgataagttacgacaattataagcaacatccctaccttcgta\n" +
                        "acgatttcactgttactgcgcttgaaatacactatggggctattggcggagagaagcaga\n" +
                        "tcgcgccgagcatatacgagacctataatgttgatgatagagaaggcgtctgaattgata\n" +
                        "catcgaagtacactttctttcgtagtatctctcgtcctctttctatctccggacacaaga\n" +
                        "attaagttatatatatagagtcttaccaatcatgttgaatcctgattctcagagttcttt\n" +
                        "ggcgggccttgtgatgactgagaaacaatgcaatattgctccaaatttcctaagcaaatt\n" +
                        "ctcggttatgttatgttatcagcaaagcgttacgttatgttatttaaatctggaatgacg\n" +
                        "gagcgaagttcttatgtcggtgtgggaataattcttttgaagacagcactccttaaataa\n" +
                        "tatcgctccgtgtttgtatttatcgaatgggtctgtaaccttgcacaagcaaatcggtgg\n" +
                        "tgtatatatcggataacaattaatacgatgttcatagtgacagtatactgatcgagtcct\n" +
                        "ctaaagtcaattacctcacttaacaatctcattgatgttgtgtcattcccggtatcgccc\n" +
                        "gtagtatgtgctctgattgaccgagtgtgaaccaaggaacatctactaatgcctttgtta\n" +
                        "ggtaagatctctctgaattccttcgtgccaacttaaaacattatcaaaatttcttctact\n" +
                        "tggattaactacttttacgagcatggcaaattcccctgtggaagacggttcattattatc\n" +
                        "ggaaaccttatagaaattgcgtgttgactgaaattagatttttattgtaagagttgcatc\n" +
                        "tttgcgattcctctggtctagcttccaatgaacagtcctcccttctattcgacatcgggt\n" +
                        "ccttcgtacatgtctttgcgatgtaataattaggttcggagtgtggccttaatgggtgca\n" +
                        "actaggaatacaacgcaaatttgctgacatgatagcaaatcggtatgccggcaccaaaac\n" +
                        "gtgctccttgcttagcttgtgaatgagactcagtagttaaataaatccatatctgcaatc\n" +
                        "gattccacaggtattgtccactatctttgaactactctaagagatacaagcttagctgag\n" +
                        "accgaggtgtatatgactacgctgatatctgtaaggtaccaatgcaggcaaagtatgcga\n" +
                        "gaagctaataccggctgtttccagctttataagattaaaatttggctgtcctggcggcct\n" +
                        "cagaattgttctatcgtaatcagttggttcattaattagctaagtacgaggtacaactta\n" +
                        "tctgtcccagaacagctccacaagtttttttacagccgaaacccctgtgtgaatcttaat\n" +
                        "atccaagcgcgttatctgattagagtttacaactcagtattttatcagtacgttttgttt\n" +
                        "ccaacattacccggtatgacaaaatgacgccacgtgtcgaataatggtctgaccaatgta\n" +
                        "ggaagtgaaaagataaatat\n";
        TestRunner.run("fasta.cint", new String[]{"1000"}, "", stdout, "", 0);
    }

    @Test
    public void fasta_gcc() throws Exception {
        String stdout = ">ONE Homo sapiens alu\n" +
                        "GGCCGGGCGCGGTGGCTCACGCCTGTAATCCCAGCACTTTGGGAGGCCGAGGCGGGCGGA\n" +
                        "TCACCTGAGGTCAGGAGTTCGAGACCAGCCTGGCCAACATGGTGAAACCCCGTCTCTACT\n" +
                        "AAAAATACAAAAATTAGCCGGGCGTGGTGGCGCGCGCCTGTAATCCCAGCTACTCGGGAG\n" +
                        "GCTGAGGCAGGAGAATCGCTTGAACCCGGGAGGCGGAGGTTGCAGTGAGCCGAGATCGCG\n" +
                        "CCACTGCACTCCAGCCTGGGCGACAGAGCGAGACTCCGTCTCAAAAAGGCCGGGCGCGGT\n" +
                        "GGCTCACGCCTGTAATCCCAGCACTTTGGGAGGCCGAGGCGGGCGGATCACCTGAGGTCA\n" +
                        "GGAGTTCGAGACCAGCCTGGCCAACATGGTGAAACCCCGTCTCTACTAAAAATACAAAAA\n" +
                        "TTAGCCGGGCGTGGTGGCGCGCGCCTGTAATCCCAGCTACTCGGGAGGCTGAGGCAGGAG\n" +
                        "AATCGCTTGAACCCGGGAGGCGGAGGTTGCAGTGAGCCGAGATCGCGCCACTGCACTCCA\n" +
                        "GCCTGGGCGACAGAGCGAGACTCCGTCTCAAAAAGGCCGGGCGCGGTGGCTCACGCCTGT\n" +
                        "AATCCCAGCACTTTGGGAGGCCGAGGCGGGCGGATCACCTGAGGTCAGGAGTTCGAGACC\n" +
                        "AGCCTGGCCAACATGGTGAAACCCCGTCTCTACTAAAAATACAAAAATTAGCCGGGCGTG\n" +
                        "GTGGCGCGCGCCTGTAATCCCAGCTACTCGGGAGGCTGAGGCAGGAGAATCGCTTGAACC\n" +
                        "CGGGAGGCGGAGGTTGCAGTGAGCCGAGATCGCGCCACTGCACTCCAGCCTGGGCGACAG\n" +
                        "AGCGAGACTCCGTCTCAAAAAGGCCGGGCGCGGTGGCTCACGCCTGTAATCCCAGCACTT\n" +
                        "TGGGAGGCCGAGGCGGGCGGATCACCTGAGGTCAGGAGTTCGAGACCAGCCTGGCCAACA\n" +
                        "TGGTGAAACCCCGTCTCTACTAAAAATACAAAAATTAGCCGGGCGTGGTGGCGCGCGCCT\n" +
                        "GTAATCCCAGCTACTCGGGAGGCTGAGGCAGGAGAATCGCTTGAACCCGGGAGGCGGAGG\n" +
                        "TTGCAGTGAGCCGAGATCGCGCCACTGCACTCCAGCCTGGGCGACAGAGCGAGACTCCGT\n" +
                        "CTCAAAAAGGCCGGGCGCGGTGGCTCACGCCTGTAATCCCAGCACTTTGGGAGGCCGAGG\n" +
                        "CGGGCGGATCACCTGAGGTCAGGAGTTCGAGACCAGCCTGGCCAACATGGTGAAACCCCG\n" +
                        "TCTCTACTAAAAATACAAAAATTAGCCGGGCGTGGTGGCGCGCGCCTGTAATCCCAGCTA\n" +
                        "CTCGGGAGGCTGAGGCAGGAGAATCGCTTGAACCCGGGAGGCGGAGGTTGCAGTGAGCCG\n" +
                        "AGATCGCGCCACTGCACTCCAGCCTGGGCGACAGAGCGAGACTCCGTCTCAAAAAGGCCG\n" +
                        "GGCGCGGTGGCTCACGCCTGTAATCCCAGCACTTTGGGAGGCCGAGGCGGGCGGATCACC\n" +
                        "TGAGGTCAGGAGTTCGAGACCAGCCTGGCCAACATGGTGAAACCCCGTCTCTACTAAAAA\n" +
                        "TACAAAAATTAGCCGGGCGTGGTGGCGCGCGCCTGTAATCCCAGCTACTCGGGAGGCTGA\n" +
                        "GGCAGGAGAATCGCTTGAACCCGGGAGGCGGAGGTTGCAGTGAGCCGAGATCGCGCCACT\n" +
                        "GCACTCCAGCCTGGGCGACAGAGCGAGACTCCGTCTCAAAAAGGCCGGGCGCGGTGGCTC\n" +
                        "ACGCCTGTAATCCCAGCACTTTGGGAGGCCGAGGCGGGCGGATCACCTGAGGTCAGGAGT\n" +
                        "TCGAGACCAGCCTGGCCAACATGGTGAAACCCCGTCTCTACTAAAAATACAAAAATTAGC\n" +
                        "CGGGCGTGGTGGCGCGCGCCTGTAATCCCAGCTACTCGGGAGGCTGAGGCAGGAGAATCG\n" +
                        "CTTGAACCCGGGAGGCGGAGGTTGCAGTGAGCCGAGATCGCGCCACTGCACTCCAGCCTG\n" +
                        "GGCGACAGAGCGAGACTCCG\n" +
                        ">TWO IUB ambiguity codes\n" +
                        "cttBtatcatatgctaKggNcataaaSatgtaaaDcDRtBggDtctttataattcBgtcg\n" +
                        "tactDtDagcctatttSVHtHttKtgtHMaSattgWaHKHttttagacatWatgtRgaaa\n" +
                        "NtactMcSMtYtcMgRtacttctWBacgaaatatagScDtttgaagacacatagtVgYgt\n" +
                        "cattHWtMMWcStgttaggKtSgaYaaccWStcgBttgcgaMttBYatcWtgacaYcaga\n" +
                        "gtaBDtRacttttcWatMttDBcatWtatcttactaBgaYtcttgttttttttYaaScYa\n" +
                        "HgtgttNtSatcMtcVaaaStccRcctDaataataStcYtRDSaMtDttgttSagtRRca\n" +
                        "tttHatSttMtWgtcgtatSSagactYaaattcaMtWatttaSgYttaRgKaRtccactt\n" +
                        "tattRggaMcDaWaWagttttgacatgttctacaaaRaatataataaMttcgDacgaSSt\n" +
                        "acaStYRctVaNMtMgtaggcKatcttttattaaaaagVWaHKYagtttttatttaacct\n" +
                        "tacgtVtcVaattVMBcttaMtttaStgacttagattWWacVtgWYagWVRctDattBYt\n" +
                        "gtttaagaagattattgacVatMaacattVctgtBSgaVtgWWggaKHaatKWcBScSWa\n" +
                        "accRVacacaaactaccScattRatatKVtactatatttHttaagtttSKtRtacaaagt\n" +
                        "RDttcaaaaWgcacatWaDgtDKacgaacaattacaRNWaatHtttStgttattaaMtgt\n" +
                        "tgDcgtMgcatBtgcttcgcgaDWgagctgcgaggggVtaaScNatttacttaatgacag\n" +
                        "cccccacatYScaMgtaggtYaNgttctgaMaacNaMRaacaaacaKctacatagYWctg\n" +
                        "ttWaaataaaataRattagHacacaagcgKatacBttRttaagtatttccgatctHSaat\n" +
                        "actcNttMaagtattMtgRtgaMgcataatHcMtaBSaRattagttgatHtMttaaKagg\n" +
                        "YtaaBataSaVatactWtataVWgKgttaaaacagtgcgRatatacatVtHRtVYataSa\n" +
                        "KtWaStVcNKHKttactatccctcatgWHatWaRcttactaggatctataDtDHBttata\n" +
                        "aaaHgtacVtagaYttYaKcctattcttcttaataNDaaggaaaDYgcggctaaWSctBa\n" +
                        "aNtgctggMBaKctaMVKagBaactaWaDaMaccYVtNtaHtVWtKgRtcaaNtYaNacg\n" +
                        "gtttNattgVtttctgtBaWgtaattcaagtcaVWtactNggattctttaYtaaagccgc\n" +
                        "tcttagHVggaYtgtNcDaVagctctctKgacgtatagYcctRYHDtgBattDaaDgccK\n" +
                        "tcHaaStttMcctagtattgcRgWBaVatHaaaataYtgtttagMDMRtaataaggatMt\n" +
                        "ttctWgtNtgtgaaaaMaatatRtttMtDgHHtgtcattttcWattRSHcVagaagtacg\n" +
                        "ggtaKVattKYagactNaatgtttgKMMgYNtcccgSKttctaStatatNVataYHgtNa\n" +
                        "BKRgNacaactgatttcctttaNcgatttctctataScaHtataRagtcRVttacDSDtt\n" +
                        "aRtSatacHgtSKacYagttMHtWataggatgactNtatSaNctataVtttRNKtgRacc\n" +
                        "tttYtatgttactttttcctttaaacatacaHactMacacggtWataMtBVacRaSaatc\n" +
                        "cgtaBVttccagccBcttaRKtgtgcctttttRtgtcagcRttKtaaacKtaaatctcac\n" +
                        "aattgcaNtSBaaccgggttattaaBcKatDagttactcttcattVtttHaaggctKKga\n" +
                        "tacatcBggScagtVcacattttgaHaDSgHatRMaHWggtatatRgccDttcgtatcga\n" +
                        "aacaHtaagttaRatgaVacttagattVKtaaYttaaatcaNatccRttRRaMScNaaaD\n" +
                        "gttVHWgtcHaaHgacVaWtgttScactaagSgttatcttagggDtaccagWattWtRtg\n" +
                        "ttHWHacgattBtgVcaYatcggttgagKcWtKKcaVtgaYgWctgYggVctgtHgaNcV\n" +
                        "taBtWaaYatcDRaaRtSctgaHaYRttagatMatgcatttNattaDttaattgttctaa\n" +
                        "ccctcccctagaWBtttHtBccttagaVaatMcBHagaVcWcagBVttcBtaYMccagat\n" +
                        "gaaaaHctctaacgttagNWRtcggattNatcRaNHttcagtKttttgWatWttcSaNgg\n" +
                        "gaWtactKKMaacatKatacNattgctWtatctaVgagctatgtRaHtYcWcttagccaa\n" +
                        "tYttWttaWSSttaHcaaaaagVacVgtaVaRMgattaVcDactttcHHggHRtgNcctt\n" +
                        "tYatcatKgctcctctatVcaaaaKaaaagtatatctgMtWtaaaacaStttMtcgactt\n" +
                        "taSatcgDataaactaaacaagtaaVctaggaSccaatMVtaaSKNVattttgHccatca\n" +
                        "cBVctgcaVatVttRtactgtVcaattHgtaaattaaattttYtatattaaRSgYtgBag\n" +
                        "aHSBDgtagcacRHtYcBgtcacttacactaYcgctWtattgSHtSatcataaatataHt\n" +
                        "cgtYaaMNgBaatttaRgaMaatatttBtttaaaHHKaatctgatWatYaacttMctctt\n" +
                        "ttVctagctDaaagtaVaKaKRtaacBgtatccaaccactHHaagaagaaggaNaaatBW\n" +
                        "attccgStaMSaMatBttgcatgRSacgttVVtaaDMtcSgVatWcaSatcttttVatag\n" +
                        "ttactttacgatcaccNtaDVgSRcgVcgtgaacgaNtaNatatagtHtMgtHcMtagaa\n" +
                        "attBgtataRaaaacaYKgtRccYtatgaagtaataKgtaaMttgaaRVatgcagaKStc\n" +
                        "tHNaaatctBBtcttaYaBWHgtVtgacagcaRcataWctcaBcYacYgatDgtDHccta\n" +
                        ">THREE Homo sapiens frequency\n" +
                        "aacacttcaccaggtatcgtgaaggctcaagattacccagagaacctttgcaatataaga\n" +
                        "atatgtatgcagcattaccctaagtaattatattctttttctgactcaaagtgacaagcc\n" +
                        "ctagtgtatattaaatcggtatatttgggaaattcctcaaactatcctaatcaggtagcc\n" +
                        "atgaaagtgatcaaaaaagttcgtacttataccatacatgaattctggccaagtaaaaaa\n" +
                        "tagattgcgcaaaattcgtaccttaagtctctcgccaagatattaggatcctattactca\n" +
                        "tatcgtgtttttctttattgccgccatccccggagtatctcacccatccttctcttaaag\n" +
                        "gcctaatattacctatgcaaataaacatatattgttgaaaattgagaacctgatcgtgat\n" +
                        "tcttatgtgtaccatatgtatagtaatcacgcgactatatagtgctttagtatcgcccgt\n" +
                        "gggtgagtgaatattctgggctagcgtgagatagtttcttgtcctaatatttttcagatc\n" +
                        "gaatagcttctatttttgtgtttattgacatatgtcgaaactccttactcagtgaaagtc\n" +
                        "atgaccagatccacgaacaatcttcggaatcagtctcgttttacggcggaatcttgagtc\n" +
                        "taacttatatcccgtcgcttactttctaacaccccttatgtatttttaaaattacgttta\n" +
                        "ttcgaacgtacttggcggaagcgttattttttgaagtaagttacattgggcagactcttg\n" +
                        "acattttcgatacgactttctttcatccatcacaggactcgttcgtattgatatcagaag\n" +
                        "ctcgtgatgattagttgtcttctttaccaatactttgaggcctattctgcgaaatttttg\n" +
                        "ttgccctgcgaacttcacataccaaggaacacctcgcaacatgccttcatatccatcgtt\n" +
                        "cattgtaattcttacacaatgaatcctaagtaattacatccctgcgtaaaagatggtagg\n" +
                        "ggcactgaggatatattaccaagcatttagttatgagtaatcagcaatgtttcttgtatt\n" +
                        "aagttctctaaaatagttacatcgtaatgttatctcgggttccgcgaataaacgagatag\n" +
                        "attcattatatatggccctaagcaaaaacctcctcgtattctgttggtaattagaatcac\n" +
                        "acaatacgggttgagatattaattatttgtagtacgaagagatataaaaagatgaacaat\n" +
                        "tactcaagtcaagatgtatacgggatttataataaaaatcgggtagagatctgctttgca\n" +
                        "attcagacgtgccactaaatcgtaatatgtcgcgttacatcagaaagggtaactattatt\n" +
                        "aattaataaagggcttaatcactacatattagatcttatccgatagtcttatctattcgt\n" +
                        "tgtatttttaagcggttctaattcagtcattatatcagtgctccgagttctttattattg\n" +
                        "ttttaaggatgacaaaatgcctcttgttataacgctgggagaagcagactaagagtcgga\n" +
                        "gcagttggtagaatgaggctgcaaaagacggtctcgacgaatggacagactttactaaac\n" +
                        "caatgaaagacagaagtagagcaaagtctgaagtggtatcagcttaattatgacaaccct\n" +
                        "taatacttccctttcgccgaatactggcgtggaaaggttttaaaagtcgaagtagttaga\n" +
                        "ggcatctctcgctcataaataggtagactactcgcaatccaatgtgactatgtaatactg\n" +
                        "ggaacatcagtccgcgatgcagcgtgtttatcaaccgtccccactcgcctggggagacat\n" +
                        "gagaccacccccgtggggattattagtccgcagtaatcgactcttgacaatccttttcga\n" +
                        "ttatgtcatagcaatttacgacagttcagcgaagtgactactcggcgaaatggtattact\n" +
                        "aaagcattcgaacccacatgaatgtgattcttggcaatttctaatccactaaagcttttc\n" +
                        "cgttgaatctggttgtagatatttatataagttcactaattaagatcacggtagtatatt\n" +
                        "gatagtgatgtctttgcaagaggttggccgaggaatttacggattctctattgatacaat\n" +
                        "ttgtctggcttataactcttaaggctgaaccaggcgtttttagacgacttgatcagctgt\n" +
                        "tagaatggtttggactccctctttcatgtcagtaacatttcagccgttattgttacgata\n" +
                        "tgcttgaacaatattgatctaccacacacccatagtatattttataggtcatgctgttac\n" +
                        "ctacgagcatggtattccacttcccattcaatgagtattcaacatcactagcctcagaga\n" +
                        "tgatgacccacctctaataacgtcacgttgcggccatgtgaaacctgaacttgagtagac\n" +
                        "gatatcaagcgctttaaattgcatataacatttgagggtaaagctaagcggatgctttat\n" +
                        "ataatcaatactcaataataagatttgattgcattttagagttatgacacgacatagttc\n" +
                        "actaacgagttactattcccagatctagactgaagtactgatcgagacgatccttacgtc\n" +
                        "gatgatcgttagttatcgacttaggtcgggtctctagcggtattggtacttaaccggaca\n" +
                        "ctatactaataacccatgatcaaagcataacagaatacagacgataatttcgccaacata\n" +
                        "tatgtacagaccccaagcatgagaagctcattgaaagctatcattgaagtcccgctcaca\n" +
                        "atgtgtcttttccagacggtttaactggttcccgggagtcctggagtttcgacttacata\n" +
                        "aatggaaacaatgtattttgctaatttatctatagcgtcatttggaccaatacagaatat\n" +
                        "tatgttgcctagtaatccactataacccgcaagtgctgatagaaaatttttagacgattt\n" +
                        "ataaatgccccaagtatccctcccgtgaatcctccgttatactaattagtattcgttcat\n" +
                        "acgtataccgcgcatatatgaacatttggcgataaggcgcgtgaattgttacgtgacaga\n" +
                        "gatagcagtttcttgtgatatggttaacagacgtacatgaagggaaactttatatctata\n" +
                        "gtgatgcttccgtagaaataccgccactggtctgccaatgatgaagtatgtagctttagg\n" +
                        "tttgtactatgaggctttcgtttgtttgcagagtataacagttgcgagtgaaaaaccgac\n" +
                        "gaatttatactaatacgctttcactattggctacaaaatagggaagagtttcaatcatga\n" +
                        "gagggagtatatggatgctttgtagctaaaggtagaacgtatgtatatgctgccgttcat\n" +
                        "tcttgaaagatacataagcgataagttacgacaattataagcaacatccctaccttcgta\n" +
                        "acgatttcactgttactgcgcttgaaatacactatggggctattggcggagagaagcaga\n" +
                        "tcgcgccgagcatatacgagacctataatgttgatgatagagaaggcgtctgaattgata\n" +
                        "catcgaagtacactttctttcgtagtatctctcgtcctctttctatctccggacacaaga\n" +
                        "attaagttatatatatagagtcttaccaatcatgttgaatcctgattctcagagttcttt\n" +
                        "ggcgggccttgtgatgactgagaaacaatgcaatattgctccaaatttcctaagcaaatt\n" +
                        "ctcggttatgttatgttatcagcaaagcgttacgttatgttatttaaatctggaatgacg\n" +
                        "gagcgaagttcttatgtcggtgtgggaataattcttttgaagacagcactccttaaataa\n" +
                        "tatcgctccgtgtttgtatttatcgaatgggtctgtaaccttgcacaagcaaatcggtgg\n" +
                        "tgtatatatcggataacaattaatacgatgttcatagtgacagtatactgatcgagtcct\n" +
                        "ctaaagtcaattacctcacttaacaatctcattgatgttgtgtcattcccggtatcgccc\n" +
                        "gtagtatgtgctctgattgaccgagtgtgaaccaaggaacatctactaatgcctttgtta\n" +
                        "ggtaagatctctctgaattccttcgtgccaacttaaaacattatcaaaatttcttctact\n" +
                        "tggattaactacttttacgagcatggcaaattcccctgtggaagacggttcattattatc\n" +
                        "ggaaaccttatagaaattgcgtgttgactgaaattagatttttattgtaagagttgcatc\n" +
                        "tttgcgattcctctggtctagcttccaatgaacagtcctcccttctattcgacatcgggt\n" +
                        "ccttcgtacatgtctttgcgatgtaataattaggttcggagtgtggccttaatgggtgca\n" +
                        "actaggaatacaacgcaaatttgctgacatgatagcaaatcggtatgccggcaccaaaac\n" +
                        "gtgctccttgcttagcttgtgaatgagactcagtagttaaataaatccatatctgcaatc\n" +
                        "gattccacaggtattgtccactatctttgaactactctaagagatacaagcttagctgag\n" +
                        "accgaggtgtatatgactacgctgatatctgtaaggtaccaatgcaggcaaagtatgcga\n" +
                        "gaagctaataccggctgtttccagctttataagattaaaatttggctgtcctggcggcct\n" +
                        "cagaattgttctatcgtaatcagttggttcattaattagctaagtacgaggtacaactta\n" +
                        "tctgtcccagaacagctccacaagtttttttacagccgaaacccctgtgtgaatcttaat\n" +
                        "atccaagcgcgttatctgattagagtttacaactcagtattttatcagtacgttttgttt\n" +
                        "ccaacattacccggtatgacaaaatgacgccacgtgtcgaataatggtctgaccaatgta\n" +
                        "ggaagtgaaaagataaatat\n";
        TestRunner.run("fasta.gcc", new String[0], "", stdout, "", 0);
    }

    @Test
    public void fasta_gcc4() throws Exception {
        String stdout = ">ONE Homo sapiens alu\n" +
                        "GGCCGGGCGCGGTGGCTCACGCCTGTAATCCCAGCACTTTGGGAGGCCGAGGCGGGCGGA\n" +
                        "TCACCTGAGGTCAGGAGTTCGAGACCAGCCTGGCCAACATGGTGAAACCCCGTCTCTACT\n" +
                        "AAAAATACAAAAATTAGCCGGGCGTGGTGGCGCGCGCCTGTAATCCCAGCTACTCGGGAG\n" +
                        "GCTGAGGCAGGAGAATCGCTTGAACCCGGGAGGCGGAGGTTGCAGTGAGCCGAGATCGCG\n" +
                        "CCACTGCACTCCAGCCTGGGCGACAGAGCGAGACTCCGTCTCAAAAAGGCCGGGCGCGGT\n" +
                        "GGCTCACGCCTGTAATCCCAGCACTTTGGGAGGCCGAGGCGGGCGGATCACCTGAGGTCA\n" +
                        "GGAGTTCGAGACCAGCCTGGCCAACATGGTGAAACCCCGTCTCTACTAAAAATACAAAAA\n" +
                        "TTAGCCGGGCGTGGTGGCGCGCGCCTGTAATCCCAGCTACTCGGGAGGCTGAGGCAGGAG\n" +
                        "AATCGCTTGAACCCGGGAGGCGGAGGTTGCAGTGAGCCGAGATCGCGCCACTGCACTCCA\n" +
                        "GCCTGGGCGACAGAGCGAGACTCCGTCTCAAAAAGGCCGGGCGCGGTGGCTCACGCCTGT\n" +
                        "AATCCCAGCACTTTGGGAGGCCGAGGCGGGCGGATCACCTGAGGTCAGGAGTTCGAGACC\n" +
                        "AGCCTGGCCAACATGGTGAAACCCCGTCTCTACTAAAAATACAAAAATTAGCCGGGCGTG\n" +
                        "GTGGCGCGCGCCTGTAATCCCAGCTACTCGGGAGGCTGAGGCAGGAGAATCGCTTGAACC\n" +
                        "CGGGAGGCGGAGGTTGCAGTGAGCCGAGATCGCGCCACTGCACTCCAGCCTGGGCGACAG\n" +
                        "AGCGAGACTCCGTCTCAAAAAGGCCGGGCGCGGTGGCTCACGCCTGTAATCCCAGCACTT\n" +
                        "TGGGAGGCCGAGGCGGGCGGATCACCTGAGGTCAGGAGTTCGAGACCAGCCTGGCCAACA\n" +
                        "TGGTGAAACCCCGTCTCTACTAAAAATACAAAAATTAGCCGGGCGTGGTGGCGCGCGCCT\n" +
                        "GTAATCCCAGCTACTCGGGAGGCTGAGGCAGGAGAATCGCTTGAACCCGGGAGGCGGAGG\n" +
                        "TTGCAGTGAGCCGAGATCGCGCCACTGCACTCCAGCCTGGGCGACAGAGCGAGACTCCGT\n" +
                        "CTCAAAAAGGCCGGGCGCGGTGGCTCACGCCTGTAATCCCAGCACTTTGGGAGGCCGAGG\n" +
                        "CGGGCGGATCACCTGAGGTCAGGAGTTCGAGACCAGCCTGGCCAACATGGTGAAACCCCG\n" +
                        "TCTCTACTAAAAATACAAAAATTAGCCGGGCGTGGTGGCGCGCGCCTGTAATCCCAGCTA\n" +
                        "CTCGGGAGGCTGAGGCAGGAGAATCGCTTGAACCCGGGAGGCGGAGGTTGCAGTGAGCCG\n" +
                        "AGATCGCGCCACTGCACTCCAGCCTGGGCGACAGAGCGAGACTCCGTCTCAAAAAGGCCG\n" +
                        "GGCGCGGTGGCTCACGCCTGTAATCCCAGCACTTTGGGAGGCCGAGGCGGGCGGATCACC\n" +
                        "TGAGGTCAGGAGTTCGAGACCAGCCTGGCCAACATGGTGAAACCCCGTCTCTACTAAAAA\n" +
                        "TACAAAAATTAGCCGGGCGTGGTGGCGCGCGCCTGTAATCCCAGCTACTCGGGAGGCTGA\n" +
                        "GGCAGGAGAATCGCTTGAACCCGGGAGGCGGAGGTTGCAGTGAGCCGAGATCGCGCCACT\n" +
                        "GCACTCCAGCCTGGGCGACAGAGCGAGACTCCGTCTCAAAAAGGCCGGGCGCGGTGGCTC\n" +
                        "ACGCCTGTAATCCCAGCACTTTGGGAGGCCGAGGCGGGCGGATCACCTGAGGTCAGGAGT\n" +
                        "TCGAGACCAGCCTGGCCAACATGGTGAAACCCCGTCTCTACTAAAAATACAAAAATTAGC\n" +
                        "CGGGCGTGGTGGCGCGCGCCTGTAATCCCAGCTACTCGGGAGGCTGAGGCAGGAGAATCG\n" +
                        "CTTGAACCCGGGAGGCGGAGGTTGCAGTGAGCCGAGATCGCGCCACTGCACTCCAGCCTG\n" +
                        "GGCGACAGAGCGAGACTCCG\n" +
                        ">TWO IUB ambiguity codes\n" +
                        "cttBtatcatatgctaKggNcataaaSatgtaaaDcDRtBggDtctttataattcBgtcg\n" +
                        "tactDtDagcctatttSVHtHttKtgtHMaSattgWaHKHttttagacatWatgtRgaaa\n" +
                        "NtactMcSMtYtcMgRtacttctWBacgaaatatagScDtttgaagacacatagtVgYgt\n" +
                        "cattHWtMMWcStgttaggKtSgaYaaccWStcgBttgcgaMttBYatcWtgacaYcaga\n" +
                        "gtaBDtRacttttcWatMttDBcatWtatcttactaBgaYtcttgttttttttYaaScYa\n" +
                        "HgtgttNtSatcMtcVaaaStccRcctDaataataStcYtRDSaMtDttgttSagtRRca\n" +
                        "tttHatSttMtWgtcgtatSSagactYaaattcaMtWatttaSgYttaRgKaRtccactt\n" +
                        "tattRggaMcDaWaWagttttgacatgttctacaaaRaatataataaMttcgDacgaSSt\n" +
                        "acaStYRctVaNMtMgtaggcKatcttttattaaaaagVWaHKYagtttttatttaacct\n" +
                        "tacgtVtcVaattVMBcttaMtttaStgacttagattWWacVtgWYagWVRctDattBYt\n" +
                        "gtttaagaagattattgacVatMaacattVctgtBSgaVtgWWggaKHaatKWcBScSWa\n" +
                        "accRVacacaaactaccScattRatatKVtactatatttHttaagtttSKtRtacaaagt\n" +
                        "RDttcaaaaWgcacatWaDgtDKacgaacaattacaRNWaatHtttStgttattaaMtgt\n" +
                        "tgDcgtMgcatBtgcttcgcgaDWgagctgcgaggggVtaaScNatttacttaatgacag\n" +
                        "cccccacatYScaMgtaggtYaNgttctgaMaacNaMRaacaaacaKctacatagYWctg\n" +
                        "ttWaaataaaataRattagHacacaagcgKatacBttRttaagtatttccgatctHSaat\n" +
                        "actcNttMaagtattMtgRtgaMgcataatHcMtaBSaRattagttgatHtMttaaKagg\n" +
                        "YtaaBataSaVatactWtataVWgKgttaaaacagtgcgRatatacatVtHRtVYataSa\n" +
                        "KtWaStVcNKHKttactatccctcatgWHatWaRcttactaggatctataDtDHBttata\n" +
                        "aaaHgtacVtagaYttYaKcctattcttcttaataNDaaggaaaDYgcggctaaWSctBa\n" +
                        "aNtgctggMBaKctaMVKagBaactaWaDaMaccYVtNtaHtVWtKgRtcaaNtYaNacg\n" +
                        "gtttNattgVtttctgtBaWgtaattcaagtcaVWtactNggattctttaYtaaagccgc\n" +
                        "tcttagHVggaYtgtNcDaVagctctctKgacgtatagYcctRYHDtgBattDaaDgccK\n" +
                        "tcHaaStttMcctagtattgcRgWBaVatHaaaataYtgtttagMDMRtaataaggatMt\n" +
                        "ttctWgtNtgtgaaaaMaatatRtttMtDgHHtgtcattttcWattRSHcVagaagtacg\n" +
                        "ggtaKVattKYagactNaatgtttgKMMgYNtcccgSKttctaStatatNVataYHgtNa\n" +
                        "BKRgNacaactgatttcctttaNcgatttctctataScaHtataRagtcRVttacDSDtt\n" +
                        "aRtSatacHgtSKacYagttMHtWataggatgactNtatSaNctataVtttRNKtgRacc\n" +
                        "tttYtatgttactttttcctttaaacatacaHactMacacggtWataMtBVacRaSaatc\n" +
                        "cgtaBVttccagccBcttaRKtgtgcctttttRtgtcagcRttKtaaacKtaaatctcac\n" +
                        "aattgcaNtSBaaccgggttattaaBcKatDagttactcttcattVtttHaaggctKKga\n" +
                        "tacatcBggScagtVcacattttgaHaDSgHatRMaHWggtatatRgccDttcgtatcga\n" +
                        "aacaHtaagttaRatgaVacttagattVKtaaYttaaatcaNatccRttRRaMScNaaaD\n" +
                        "gttVHWgtcHaaHgacVaWtgttScactaagSgttatcttagggDtaccagWattWtRtg\n" +
                        "ttHWHacgattBtgVcaYatcggttgagKcWtKKcaVtgaYgWctgYggVctgtHgaNcV\n" +
                        "taBtWaaYatcDRaaRtSctgaHaYRttagatMatgcatttNattaDttaattgttctaa\n" +
                        "ccctcccctagaWBtttHtBccttagaVaatMcBHagaVcWcagBVttcBtaYMccagat\n" +
                        "gaaaaHctctaacgttagNWRtcggattNatcRaNHttcagtKttttgWatWttcSaNgg\n" +
                        "gaWtactKKMaacatKatacNattgctWtatctaVgagctatgtRaHtYcWcttagccaa\n" +
                        "tYttWttaWSSttaHcaaaaagVacVgtaVaRMgattaVcDactttcHHggHRtgNcctt\n" +
                        "tYatcatKgctcctctatVcaaaaKaaaagtatatctgMtWtaaaacaStttMtcgactt\n" +
                        "taSatcgDataaactaaacaagtaaVctaggaSccaatMVtaaSKNVattttgHccatca\n" +
                        "cBVctgcaVatVttRtactgtVcaattHgtaaattaaattttYtatattaaRSgYtgBag\n" +
                        "aHSBDgtagcacRHtYcBgtcacttacactaYcgctWtattgSHtSatcataaatataHt\n" +
                        "cgtYaaMNgBaatttaRgaMaatatttBtttaaaHHKaatctgatWatYaacttMctctt\n" +
                        "ttVctagctDaaagtaVaKaKRtaacBgtatccaaccactHHaagaagaaggaNaaatBW\n" +
                        "attccgStaMSaMatBttgcatgRSacgttVVtaaDMtcSgVatWcaSatcttttVatag\n" +
                        "ttactttacgatcaccNtaDVgSRcgVcgtgaacgaNtaNatatagtHtMgtHcMtagaa\n" +
                        "attBgtataRaaaacaYKgtRccYtatgaagtaataKgtaaMttgaaRVatgcagaKStc\n" +
                        "tHNaaatctBBtcttaYaBWHgtVtgacagcaRcataWctcaBcYacYgatDgtDHccta\n" +
                        ">THREE Homo sapiens frequency\n" +
                        "aacacttcaccaggtatcgtgaaggctcaagattacccagagaacctttgcaatataaga\n" +
                        "atatgtatgcagcattaccctaagtaattatattctttttctgactcaaagtgacaagcc\n" +
                        "ctagtgtatattaaatcggtatatttgggaaattcctcaaactatcctaatcaggtagcc\n" +
                        "atgaaagtgatcaaaaaagttcgtacttataccatacatgaattctggccaagtaaaaaa\n" +
                        "tagattgcgcaaaattcgtaccttaagtctctcgccaagatattaggatcctattactca\n" +
                        "tatcgtgtttttctttattgccgccatccccggagtatctcacccatccttctcttaaag\n" +
                        "gcctaatattacctatgcaaataaacatatattgttgaaaattgagaacctgatcgtgat\n" +
                        "tcttatgtgtaccatatgtatagtaatcacgcgactatatagtgctttagtatcgcccgt\n" +
                        "gggtgagtgaatattctgggctagcgtgagatagtttcttgtcctaatatttttcagatc\n" +
                        "gaatagcttctatttttgtgtttattgacatatgtcgaaactccttactcagtgaaagtc\n" +
                        "atgaccagatccacgaacaatcttcggaatcagtctcgttttacggcggaatcttgagtc\n" +
                        "taacttatatcccgtcgcttactttctaacaccccttatgtatttttaaaattacgttta\n" +
                        "ttcgaacgtacttggcggaagcgttattttttgaagtaagttacattgggcagactcttg\n" +
                        "acattttcgatacgactttctttcatccatcacaggactcgttcgtattgatatcagaag\n" +
                        "ctcgtgatgattagttgtcttctttaccaatactttgaggcctattctgcgaaatttttg\n" +
                        "ttgccctgcgaacttcacataccaaggaacacctcgcaacatgccttcatatccatcgtt\n" +
                        "cattgtaattcttacacaatgaatcctaagtaattacatccctgcgtaaaagatggtagg\n" +
                        "ggcactgaggatatattaccaagcatttagttatgagtaatcagcaatgtttcttgtatt\n" +
                        "aagttctctaaaatagttacatcgtaatgttatctcgggttccgcgaataaacgagatag\n" +
                        "attcattatatatggccctaagcaaaaacctcctcgtattctgttggtaattagaatcac\n" +
                        "acaatacgggttgagatattaattatttgtagtacgaagagatataaaaagatgaacaat\n" +
                        "tactcaagtcaagatgtatacgggatttataataaaaatcgggtagagatctgctttgca\n" +
                        "attcagacgtgccactaaatcgtaatatgtcgcgttacatcagaaagggtaactattatt\n" +
                        "aattaataaagggcttaatcactacatattagatcttatccgatagtcttatctattcgt\n" +
                        "tgtatttttaagcggttctaattcagtcattatatcagtgctccgagttctttattattg\n" +
                        "ttttaaggatgacaaaatgcctcttgttataacgctgggagaagcagactaagagtcgga\n" +
                        "gcagttggtagaatgaggctgcaaaagacggtctcgacgaatggacagactttactaaac\n" +
                        "caatgaaagacagaagtagagcaaagtctgaagtggtatcagcttaattatgacaaccct\n" +
                        "taatacttccctttcgccgaatactggcgtggaaaggttttaaaagtcgaagtagttaga\n" +
                        "ggcatctctcgctcataaataggtagactactcgcaatccaatgtgactatgtaatactg\n" +
                        "ggaacatcagtccgcgatgcagcgtgtttatcaaccgtccccactcgcctggggagacat\n" +
                        "gagaccacccccgtggggattattagtccgcagtaatcgactcttgacaatccttttcga\n" +
                        "ttatgtcatagcaatttacgacagttcagcgaagtgactactcggcgaaatggtattact\n" +
                        "aaagcattcgaacccacatgaatgtgattcttggcaatttctaatccactaaagcttttc\n" +
                        "cgttgaatctggttgtagatatttatataagttcactaattaagatcacggtagtatatt\n" +
                        "gatagtgatgtctttgcaagaggttggccgaggaatttacggattctctattgatacaat\n" +
                        "ttgtctggcttataactcttaaggctgaaccaggcgtttttagacgacttgatcagctgt\n" +
                        "tagaatggtttggactccctctttcatgtcagtaacatttcagccgttattgttacgata\n" +
                        "tgcttgaacaatattgatctaccacacacccatagtatattttataggtcatgctgttac\n" +
                        "ctacgagcatggtattccacttcccattcaatgagtattcaacatcactagcctcagaga\n" +
                        "tgatgacccacctctaataacgtcacgttgcggccatgtgaaacctgaacttgagtagac\n" +
                        "gatatcaagcgctttaaattgcatataacatttgagggtaaagctaagcggatgctttat\n" +
                        "ataatcaatactcaataataagatttgattgcattttagagttatgacacgacatagttc\n" +
                        "actaacgagttactattcccagatctagactgaagtactgatcgagacgatccttacgtc\n" +
                        "gatgatcgttagttatcgacttaggtcgggtctctagcggtattggtacttaaccggaca\n" +
                        "ctatactaataacccatgatcaaagcataacagaatacagacgataatttcgccaacata\n" +
                        "tatgtacagaccccaagcatgagaagctcattgaaagctatcattgaagtcccgctcaca\n" +
                        "atgtgtcttttccagacggtttaactggttcccgggagtcctggagtttcgacttacata\n" +
                        "aatggaaacaatgtattttgctaatttatctatagcgtcatttggaccaatacagaatat\n" +
                        "tatgttgcctagtaatccactataacccgcaagtgctgatagaaaatttttagacgattt\n" +
                        "ataaatgccccaagtatccctcccgtgaatcctccgttatactaattagtattcgttcat\n" +
                        "acgtataccgcgcatatatgaacatttggcgataaggcgcgtgaattgttacgtgacaga\n" +
                        "gatagcagtttcttgtgatatggttaacagacgtacatgaagggaaactttatatctata\n" +
                        "gtgatgcttccgtagaaataccgccactggtctgccaatgatgaagtatgtagctttagg\n" +
                        "tttgtactatgaggctttcgtttgtttgcagagtataacagttgcgagtgaaaaaccgac\n" +
                        "gaatttatactaatacgctttcactattggctacaaaatagggaagagtttcaatcatga\n" +
                        "gagggagtatatggatgctttgtagctaaaggtagaacgtatgtatatgctgccgttcat\n" +
                        "tcttgaaagatacataagcgataagttacgacaattataagcaacatccctaccttcgta\n" +
                        "acgatttcactgttactgcgcttgaaatacactatggggctattggcggagagaagcaga\n" +
                        "tcgcgccgagcatatacgagacctataatgttgatgatagagaaggcgtctgaattgata\n" +
                        "catcgaagtacactttctttcgtagtatctctcgtcctctttctatctccggacacaaga\n" +
                        "attaagttatatatatagagtcttaccaatcatgttgaatcctgattctcagagttcttt\n" +
                        "ggcgggccttgtgatgactgagaaacaatgcaatattgctccaaatttcctaagcaaatt\n" +
                        "ctcggttatgttatgttatcagcaaagcgttacgttatgttatttaaatctggaatgacg\n" +
                        "gagcgaagttcttatgtcggtgtgggaataattcttttgaagacagcactccttaaataa\n" +
                        "tatcgctccgtgtttgtatttatcgaatgggtctgtaaccttgcacaagcaaatcggtgg\n" +
                        "tgtatatatcggataacaattaatacgatgttcatagtgacagtatactgatcgagtcct\n" +
                        "ctaaagtcaattacctcacttaacaatctcattgatgttgtgtcattcccggtatcgccc\n" +
                        "gtagtatgtgctctgattgaccgagtgtgaaccaaggaacatctactaatgcctttgtta\n" +
                        "ggtaagatctctctgaattccttcgtgccaacttaaaacattatcaaaatttcttctact\n" +
                        "tggattaactacttttacgagcatggcaaattcccctgtggaagacggttcattattatc\n" +
                        "ggaaaccttatagaaattgcgtgttgactgaaattagatttttattgtaagagttgcatc\n" +
                        "tttgcgattcctctggtctagcttccaatgaacagtcctcccttctattcgacatcgggt\n" +
                        "ccttcgtacatgtctttgcgatgtaataattaggttcggagtgtggccttaatgggtgca\n" +
                        "actaggaatacaacgcaaatttgctgacatgatagcaaatcggtatgccggcaccaaaac\n" +
                        "gtgctccttgcttagcttgtgaatgagactcagtagttaaataaatccatatctgcaatc\n" +
                        "gattccacaggtattgtccactatctttgaactactctaagagatacaagcttagctgag\n" +
                        "accgaggtgtatatgactacgctgatatctgtaaggtaccaatgcaggcaaagtatgcga\n" +
                        "gaagctaataccggctgtttccagctttataagattaaaatttggctgtcctggcggcct\n" +
                        "cagaattgttctatcgtaatcagttggttcattaattagctaagtacgaggtacaactta\n" +
                        "tctgtcccagaacagctccacaagtttttttacagccgaaacccctgtgtgaatcttaat\n" +
                        "atccaagcgcgttatctgattagagtttacaactcagtattttatcagtacgttttgttt\n" +
                        "ccaacattacccggtatgacaaaatgacgccacgtgtcgaataatggtctgaccaatgta\n" +
                        "ggaagtgaaaagataaatat\n";
        TestRunner.run("fasta.gcc-4", new String[0], "", stdout, "", 0);
    }

    @Test
    public void fannkuchredux_cint() throws Exception {
        TestRunner.run("fannkuchredux.cint", new String[]{"7"}, "", "228\nPfannkuchen(7) = 16\n", "", 0);
    }

    @Test
    public void fannkuchredux_gcc() throws Exception {
        TestRunner.run("fannkuchredux.gcc", new String[]{"7"}, "", "228\nPfannkuchen(7) = 16\n", "", 0);
    }

    @Test
    public void fannkuchredux_gcc3() throws Exception {
        TestRunner.run("fannkuchredux.gcc-3", new String[]{"7"}, "", "228\nPfannkuchen(7) = 16\n", "", 0);
    }

    @Test
    public void mandelbrot_cint() throws Exception {
        String stdout = "UDQKMjAwIDIwMAoAAAAAAAAAAAAAAAAAAAAAAAACAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHoAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAF8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAfAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHwAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAv4AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAP/4AAAAAAAAAAAAAAAAAAAAAAAAAAAAABf" +
                        "/+AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAP//0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAD///wAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAf//4AAAAAAAAAAAAAAAAAAAAAAAAAAAAAf//8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAD///gAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAF///wAAAAAAAAAAAAAAAAAAAAAAAAAAAAA///+AAAAAAAAAAAAAAAAAAAAAAAAAAAAAD///wAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAC///wAAAAAAAAAAAAAAAAAAAAAAAAAAAAA///5AAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AD///wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAf//gAAAAAAAAAAAAAAAAAAAAAAAAAAAAAD//4AAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAf/8AAAAAAAAAAAAAAAAAAAAAAAABAAEAAH/+AAAAAAAAAAAAAAAAAAAAAAAAAAAAAwAf+ARggAAAAAAA" +
                        "AAAAAAAAAAAAAAAACQY////WAAAAAAAAAAAAAAAAAAAAAAAAAAGjP////sAgAAAAAAAAAAAAAAAAAAAAAAgF6//////" +
                        "p4AAAAAAAAAAAAAAAAAAAAAUIB///////+fAAAAAAAAAAAAAAAAAAAAALGAP///////3gAQAAAAAAAAAAAAAAAAAAC/" +
                        "w2////////wAAAAAAAAAAAAAAAAAAAAAH8H/////////AAAAAAAAAAAAAAAAAAAAAD/l/////////2AYkAAAAAAAAAA" +
                        "AAAAAAAE/7//////////gfuAAAAAAAAAAAAAAAAAB/9//////////6H/AAAAAAAAAAAAAAAAAAP////////////j/gA" +
                        "AAAAAAAAAAAAAAAAB////////////9/4AAAAAAAAAAAAAAAAAAH////////////v+AAAAAAAAAAAAAAAAAAAP//////" +
                        "///////wAAAAAAAAAAAAAAAAAAP/////////////+AAAAAAAAAAAAAAAAAAf/////////////8AAAAAAAAAAAAAAAAA" +
                        "Az/////////////+gAAAAAAAAAAAAAAAAAP/////////////8AAAAAAAAAAAAAAAACAB//////////////wAAAAAAAA" +
                        "AAAAAAAAEcv/////////////6AAAAAAAAAAAAAAAAB/f//////////////YAAAAAAAAAAAAAAAAn///////////////" +
                        "8AAAAAAAAAAAAAAAAI////////////////QAAAAAAAAAAAAAAAAv///////////////3AAAAAAAAAAAAAAAAD//////" +
                        "//////////gAAAAAAAAAAAAAAAAf///////////////oAAAAAAAAAAAAAAAAH///////////////8AAAAAAAAAAAAAA" +
                        "ABF////////////////hAAAAAAAAAAAAAAAB////////////////9AAAAAAAAAAAAAAAKf////////////////cAAAA" +
                        "AAAAAAAAAAH//////////////////gAAAAAAAAAAAAAAf/////////////////+AAAAAAAAAAAAAAH/////////////" +
                        "////+AAAAAAAAAAAAAAAf/////////////////AAAAAAAAAAAAAABX////////////////+gAAAAAAAAAEAAAAD////" +
                        "/////////////wAAAAAAAgACBgAA+/////////////////4AAAAAAAAAB4AAAD//////////////////AAAAAAAHoA8" +
                        "AAAAf/////////////////8AAAAAAA/GHMyAAD/////////////////+AAAAAAAfi//cAAA//////////////////gA" +
                        "AAAAAX9///AAB//////////////////4AAAAAAA////5AAP//////////////////gAAAAAAP////wAD///////////" +
                        "///////4AAAAAAC////+QD///////////////////wAAAAAA/////4AP/////////////////8AAAAAABf/////AH//" +
                        "////////////////gAAAAAAP/////4D//////////////////8AAAAADX//////g//////////////////8AAAAAAP/" +
                        "/////wH//////////////////gAAAAAH///////j//////////////////4AAAAAAf//////w//////////////////" +
                        "/gAAAAAP//////+P/////////////////+gAAAAAP///////j//////////////////wAAAAAD///////8/////////" +
                        "/////////8AAAAAAf///////P//////////////////AAAAAAP///////z/////////////////+wAAAAAD///////+" +
                        "//////////////////gAAAAZB////////v/////////////////8AAAAH+f/////////////////////////8AAAAB/" +
                        "j/////////////////////////+gAAAA////////////////////////////gAAAAP/////////////////////////" +
                        "//wAAAAH///////////////////////////wAAAAH///////////////////////////wAAA///////////////////" +
                        "//////////wAAAAAf///////////////////////////AAAAAB///////////////////////////8AAAAAP///////" +
                        "////////////////////wAAAAD///////////////////////////+AAAAAf4//////////////////////////oAAA" +
                        "AH+f/////////////////////////8AAAABkH///////+//////////////////wAAAAAA////////v////////////" +
                        "/////4AAAAAAP///////z/////////////////+wAAAAAB///////8//////////////////8AAAAAA////////P///" +
                        "///////////////AAAAAAP///////j//////////////////wAAAAAA///////4//////////////////6AAAAAAH//" +
                        "////8P//////////////////4AAAAAH///////j//////////////////4AAAAAA///////Af/////////////////+" +
                        "AAAAAA1//////4P//////////////////AAAAAAAP/////4D//////////////////8AAAAAAF/////8Af/////////" +
                        "////////+AAAAAAAP////+AD//////////////////AAAAAAAC////+QD///////////////////wAAAAAA/////AAP" +
                        "//////////////////gAAAAAAP///+QAD//////////////////4AAAAAAX9///AAB//////////////////4AAAAAA" +
                        "B+L/9wAAD/////////////////+AAAAAAAPxhzMgAA//////////////////gAAAAAAHoA8AAAAf///////////////" +
                        "//8AAAAAAAAAHgAAAP/////////////////8AAAAAAAIAAgYAAPv////////////////+AAAAAAAAAAEAAAAD//////" +
                        "///////////wAAAAAAAAAAAAAAFf////////////////6AAAAAAAAAAAAAAAH/////////////////wAAAAAAAAAAAA" +
                        "AAH/////////////////+AAAAAAAAAAAAAAB//////////////////4AAAAAAAAAAAAAB//////////////////4AAA" +
                        "AAAAAAAAAAAKf////////////////cAAAAAAAAAAAAAAAH////////////////0AAAAAAAAAAAAAAARf///////////" +
                        "////4QAAAAAAAAAAAAAAAH///////////////8AAAAAAAAAAAAAAAAB///////////////+gAAAAAAAAAAAAAAAA///" +
                        "/////////////4AAAAAAAAAAAAAAAAv///////////////3AAAAAAAAAAAAAAAAj///////////////9AAAAAAAAAAA" +
                        "AAAAAJ////////////////AAAAAAAAAAAAAAAAB/f//////////////YAAAAAAAAAAAAAAAARy//////////////oAA" +
                        "AAAAAAAAAAAAAAgAf/////////////8AAAAAAAAAAAAAAAAAAP/////////////8AAAAAAAAAAAAAAAAAADP///////" +
                        "//////6AAAAAAAAAAAAAAAAAAH//////////////AAAAAAAAAAAAAAAAAAAP/////////////+AAAAAAAAAAAAAAAAA" +
                        "AA//////////////AAAAAAAAAAAAAAAAAAB////////////7/gAAAAAAAAAAAAAAAAAB////////////9/4AAAAAAAA" +
                        "AAAAAAAAAA////////////+P+AAAAAAAAAAAAAAAAAAf/f/////////+h/wAAAAAAAAAAAAAAAAAE/7//////////gf" +
                        "uAAAAAAAAAAAAAAAAAAP+X/////////YBiQAAAAAAAAAAAAAAAAAB/B/////////wAAAAAAAAAAAAAAAAAAAAC/w2//" +
                        "//////wAAAAAAAAAAAAAAAAAAAAAsYA////////eABAAAAAAAAAAAAAAAAAAAFCAf///////nwAAAAAAAAAAAAAAAAA" +
                        "AAAAAgF6//////p4AAAAAAAAAAAAAAAAAAAAAAAAaM////+wCAAAAAAAAAAAAAAAAAAAAAAAAkGP///1gAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAwAf+ARggAAAAAAAAAAAAAAAAAAAAAEAAQAAf/4AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAH//AAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAD//4AAAAAAAAAAAAAAAAAAAAAAAAAAAAAB//+AAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAA///8AAAAAAAAAAAAAAAAAAAAAAAAAAAAA///5AAAAAAAAAAAAAAAAAAAAAAAAAAAAAL///AAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAA///8AAAAAAAAAAAAAAAAAAAAAAAAAAAAA///+AAAAAAAAAAAAAAAAAAAAAAAAAAAAAX///AAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAA///4AAAAAAAAAAAAAAAAAAAAAAAAAAAAAf//8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAB///" +
                        "gAAAAAAAAAAAAAAAAAAAAAAAAAAAAA///8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAP//0AAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAF//4AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAD/+AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAv4AAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAfAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAF8AAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAegAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAPAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA==";
        TestRunner.runBinary("mandelbrot.cint", new String[]{"200"}, "", stdout, "", 0);
    }

    @Test
    public void mandelbrot_gcc2() throws Exception {
        String stdout = "UDQKMjAwIDIwMAoAAAAAAAAAAAAAAAAAAAAAAAACAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHoAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAF8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAfAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHwAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAv4AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAP/4AAAAAAAAAAAAAAAAAAAAAAAAAAAAABf" +
                        "/+AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAP//0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAD///wAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAf//4AAAAAAAAAAAAAAAAAAAAAAAAAAAAAf//8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAD///gAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAF///wAAAAAAAAAAAAAAAAAAAAAAAAAAAAA///+AAAAAAAAAAAAAAAAAAAAAAAAAAAAAD///wAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAC///wAAAAAAAAAAAAAAAAAAAAAAAAAAAAA///5AAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AD///wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAf//gAAAAAAAAAAAAAAAAAAAAAAAAAAAAAD//4AAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAf/8AAAAAAAAAAAAAAAAAAAAAAAABAAEAAH/+AAAAAAAAAAAAAAAAAAAAAAAAAAAAAwAf+ARggAAAAAAA" +
                        "AAAAAAAAAAAAAAAACQY////WAAAAAAAAAAAAAAAAAAAAAAAAAAGjP////sAgAAAAAAAAAAAAAAAAAAAAAAgF6//////" +
                        "p4AAAAAAAAAAAAAAAAAAAAAUIB///////+fAAAAAAAAAAAAAAAAAAAAALGAP///////3gAQAAAAAAAAAAAAAAAAAAC/" +
                        "w2////////wAAAAAAAAAAAAAAAAAAAAAH8H/////////AAAAAAAAAAAAAAAAAAAAAD/l/////////2AYkAAAAAAAAAA" +
                        "AAAAAAAE/7//////////gfuAAAAAAAAAAAAAAAAAB/9//////////6H/AAAAAAAAAAAAAAAAAAP////////////j/gA" +
                        "AAAAAAAAAAAAAAAAB////////////9/4AAAAAAAAAAAAAAAAAAH////////////v+AAAAAAAAAAAAAAAAAAAP//////" +
                        "///////wAAAAAAAAAAAAAAAAAAP/////////////+AAAAAAAAAAAAAAAAAAf/////////////8AAAAAAAAAAAAAAAAA" +
                        "Az/////////////+gAAAAAAAAAAAAAAAAAP/////////////8AAAAAAAAAAAAAAAACAB//////////////wAAAAAAAA" +
                        "AAAAAAAAEcv/////////////6AAAAAAAAAAAAAAAAB/f//////////////YAAAAAAAAAAAAAAAAn///////////////" +
                        "8AAAAAAAAAAAAAAAAI////////////////QAAAAAAAAAAAAAAAAv///////////////3AAAAAAAAAAAAAAAAD//////" +
                        "//////////gAAAAAAAAAAAAAAAAf///////////////oAAAAAAAAAAAAAAAAH///////////////8AAAAAAAAAAAAAA" +
                        "ABF////////////////hAAAAAAAAAAAAAAAB////////////////9AAAAAAAAAAAAAAAKf////////////////cAAAA" +
                        "AAAAAAAAAAH//////////////////gAAAAAAAAAAAAAAf/////////////////+AAAAAAAAAAAAAAH/////////////" +
                        "////+AAAAAAAAAAAAAAAf/////////////////AAAAAAAAAAAAAABX////////////////+gAAAAAAAAAEAAAAD////" +
                        "/////////////wAAAAAAAgACBgAA+/////////////////4AAAAAAAAAB4AAAD//////////////////AAAAAAAHoA8" +
                        "AAAAf/////////////////8AAAAAAA/GHMyAAD/////////////////+AAAAAAAfi//cAAA//////////////////gA" +
                        "AAAAAX9///AAB//////////////////4AAAAAAA////5AAP//////////////////gAAAAAAP////wAD///////////" +
                        "///////4AAAAAAC////+QD///////////////////wAAAAAA/////4AP/////////////////8AAAAAABf/////AH//" +
                        "////////////////gAAAAAAP/////4D//////////////////8AAAAADX//////g//////////////////8AAAAAAP/" +
                        "/////wH//////////////////gAAAAAH///////j//////////////////4AAAAAAf//////w//////////////////" +
                        "/gAAAAAP//////+P/////////////////+gAAAAAP///////j//////////////////wAAAAAD///////8/////////" +
                        "/////////8AAAAAAf///////P//////////////////AAAAAAP///////z/////////////////+wAAAAAD///////+" +
                        "//////////////////gAAAAZB////////v/////////////////8AAAAH+f/////////////////////////8AAAAB/" +
                        "j/////////////////////////+gAAAA////////////////////////////gAAAAP/////////////////////////" +
                        "//wAAAAH///////////////////////////wAAAAH///////////////////////////wAAA///////////////////" +
                        "//////////wAAAAAf///////////////////////////AAAAAB///////////////////////////8AAAAAP///////" +
                        "////////////////////wAAAAD///////////////////////////+AAAAAf4//////////////////////////oAAA" +
                        "AH+f/////////////////////////8AAAABkH///////+//////////////////wAAAAAA////////v////////////" +
                        "/////4AAAAAAP///////z/////////////////+wAAAAAB///////8//////////////////8AAAAAA////////P///" +
                        "///////////////AAAAAAP///////j//////////////////wAAAAAA///////4//////////////////6AAAAAAH//" +
                        "////8P//////////////////4AAAAAH///////j//////////////////4AAAAAA///////Af/////////////////+" +
                        "AAAAAA1//////4P//////////////////AAAAAAAP/////4D//////////////////8AAAAAAF/////8Af/////////" +
                        "////////+AAAAAAAP////+AD//////////////////AAAAAAAC////+QD///////////////////wAAAAAA/////AAP" +
                        "//////////////////gAAAAAAP///+QAD//////////////////4AAAAAAX9///AAB//////////////////4AAAAAA" +
                        "B+L/9wAAD/////////////////+AAAAAAAPxhzMgAA//////////////////gAAAAAAHoA8AAAAf///////////////" +
                        "//8AAAAAAAAAHgAAAP/////////////////8AAAAAAAIAAgYAAPv////////////////+AAAAAAAAAAEAAAAD//////" +
                        "///////////wAAAAAAAAAAAAAAFf////////////////6AAAAAAAAAAAAAAAH/////////////////wAAAAAAAAAAAA" +
                        "AAH/////////////////+AAAAAAAAAAAAAAB//////////////////4AAAAAAAAAAAAAB//////////////////4AAA" +
                        "AAAAAAAAAAAKf////////////////cAAAAAAAAAAAAAAAH////////////////0AAAAAAAAAAAAAAARf///////////" +
                        "////4QAAAAAAAAAAAAAAAH///////////////8AAAAAAAAAAAAAAAAB///////////////+gAAAAAAAAAAAAAAAA///" +
                        "/////////////4AAAAAAAAAAAAAAAAv///////////////3AAAAAAAAAAAAAAAAj///////////////9AAAAAAAAAAA" +
                        "AAAAAJ////////////////AAAAAAAAAAAAAAAAB/f//////////////YAAAAAAAAAAAAAAAARy//////////////oAA" +
                        "AAAAAAAAAAAAAAgAf/////////////8AAAAAAAAAAAAAAAAAAP/////////////8AAAAAAAAAAAAAAAAAADP///////" +
                        "//////6AAAAAAAAAAAAAAAAAAH//////////////AAAAAAAAAAAAAAAAAAAP/////////////+AAAAAAAAAAAAAAAAA" +
                        "AA//////////////AAAAAAAAAAAAAAAAAAB////////////7/gAAAAAAAAAAAAAAAAAB////////////9/4AAAAAAAA" +
                        "AAAAAAAAAA////////////+P+AAAAAAAAAAAAAAAAAAf/f/////////+h/wAAAAAAAAAAAAAAAAAE/7//////////gf" +
                        "uAAAAAAAAAAAAAAAAAAP+X/////////YBiQAAAAAAAAAAAAAAAAAB/B/////////wAAAAAAAAAAAAAAAAAAAAC/w2//" +
                        "//////wAAAAAAAAAAAAAAAAAAAAAsYA////////eABAAAAAAAAAAAAAAAAAAAFCAf///////nwAAAAAAAAAAAAAAAAA" +
                        "AAAAAgF6//////p4AAAAAAAAAAAAAAAAAAAAAAAAaM////+wCAAAAAAAAAAAAAAAAAAAAAAAAkGP///1gAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAwAf+ARggAAAAAAAAAAAAAAAAAAAAAEAAQAAf/4AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAH//AAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAD//4AAAAAAAAAAAAAAAAAAAAAAAAAAAAAB//+AAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAA///8AAAAAAAAAAAAAAAAAAAAAAAAAAAAA///5AAAAAAAAAAAAAAAAAAAAAAAAAAAAAL///AAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAA///8AAAAAAAAAAAAAAAAAAAAAAAAAAAAA///+AAAAAAAAAAAAAAAAAAAAAAAAAAAAAX///AAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAA///4AAAAAAAAAAAAAAAAAAAAAAAAAAAAAf//8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAB///" +
                        "gAAAAAAAAAAAAAAAAAAAAAAAAAAAAA///8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAP//0AAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAF//4AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAD/+AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAv4AAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAfAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAF8AAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAegAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAPAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA==";
        TestRunner.runBinary("mandelbrot.gcc-2", new String[]{"200"}, "", stdout, "", 0);
    }

    @Test
    public void mandelbrot_gcc4() throws Exception {
        String stdout = "UDQKMjAwIDIwMAoAAAAAAAAAAAAAAAAAAAAAAAACAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHoAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAF8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAfAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHwAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAv4AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAP/4AAAAAAAAAAAAAAAAAAAAAAAAAAAAABf" +
                        "/+AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAP//0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAD///wAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAf//4AAAAAAAAAAAAAAAAAAAAAAAAAAAAAf//8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAD///gAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAF///wAAAAAAAAAAAAAAAAAAAAAAAAAAAAA///+AAAAAAAAAAAAAAAAAAAAAAAAAAAAAD///wAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAC///wAAAAAAAAAAAAAAAAAAAAAAAAAAAAA///5AAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AD///wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAf//gAAAAAAAAAAAAAAAAAAAAAAAAAAAAAD//4AAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAf/8AAAAAAAAAAAAAAAAAAAAAAAABAAEAAH/+AAAAAAAAAAAAAAAAAAAAAAAAAAAAAwAf+ARggAAAAAAA" +
                        "AAAAAAAAAAAAAAAACQY////WAAAAAAAAAAAAAAAAAAAAAAAAAAGjP////sAgAAAAAAAAAAAAAAAAAAAAAAgF6//////" +
                        "p4AAAAAAAAAAAAAAAAAAAAAUIB///////+fAAAAAAAAAAAAAAAAAAAAALGAP///////3gAQAAAAAAAAAAAAAAAAAAC/" +
                        "w2////////wAAAAAAAAAAAAAAAAAAAAAH8H/////////AAAAAAAAAAAAAAAAAAAAAD/l/////////2AYkAAAAAAAAAA" +
                        "AAAAAAAE/7//////////gfuAAAAAAAAAAAAAAAAAB/9//////////6H/AAAAAAAAAAAAAAAAAAP////////////j/gA" +
                        "AAAAAAAAAAAAAAAAB////////////9/4AAAAAAAAAAAAAAAAAAH////////////v+AAAAAAAAAAAAAAAAAAAP//////" +
                        "///////wAAAAAAAAAAAAAAAAAAP/////////////+AAAAAAAAAAAAAAAAAAf/////////////8AAAAAAAAAAAAAAAAA" +
                        "Az/////////////+gAAAAAAAAAAAAAAAAAP/////////////8AAAAAAAAAAAAAAAACAB//////////////wAAAAAAAA" +
                        "AAAAAAAAEcv/////////////6AAAAAAAAAAAAAAAAB/f//////////////YAAAAAAAAAAAAAAAAn///////////////" +
                        "8AAAAAAAAAAAAAAAAI////////////////QAAAAAAAAAAAAAAAAv///////////////3AAAAAAAAAAAAAAAAD//////" +
                        "//////////gAAAAAAAAAAAAAAAAf///////////////oAAAAAAAAAAAAAAAAH///////////////8AAAAAAAAAAAAAA" +
                        "ABF////////////////hAAAAAAAAAAAAAAAB////////////////9AAAAAAAAAAAAAAAKf////////////////cAAAA" +
                        "AAAAAAAAAAH//////////////////gAAAAAAAAAAAAAAf/////////////////+AAAAAAAAAAAAAAH/////////////" +
                        "////+AAAAAAAAAAAAAAAf/////////////////AAAAAAAAAAAAAABX////////////////+gAAAAAAAAAEAAAAD////" +
                        "/////////////wAAAAAAAgACBgAA+/////////////////4AAAAAAAAAB4AAAD//////////////////AAAAAAAHoA8" +
                        "AAAAf/////////////////8AAAAAAA/GHMyAAD/////////////////+AAAAAAAfi//cAAA//////////////////gA" +
                        "AAAAAX9///AAB//////////////////4AAAAAAA////5AAP//////////////////gAAAAAAP////wAD///////////" +
                        "///////4AAAAAAC////+QD///////////////////wAAAAAA/////4AP/////////////////8AAAAAABf/////AH//" +
                        "////////////////gAAAAAAP/////4D//////////////////8AAAAADX//////g//////////////////8AAAAAAP/" +
                        "/////wH//////////////////gAAAAAH///////j//////////////////4AAAAAAf//////w//////////////////" +
                        "/gAAAAAP//////+P/////////////////+gAAAAAP///////j//////////////////wAAAAAD///////8/////////" +
                        "/////////8AAAAAAf///////P//////////////////AAAAAAP///////z/////////////////+wAAAAAD///////+" +
                        "//////////////////gAAAAZB////////v/////////////////8AAAAH+f/////////////////////////8AAAAB/" +
                        "j/////////////////////////+gAAAA////////////////////////////gAAAAP/////////////////////////" +
                        "//wAAAAH///////////////////////////wAAAAH///////////////////////////wAAA///////////////////" +
                        "//////////wAAAAAf///////////////////////////AAAAAB///////////////////////////8AAAAAP///////" +
                        "////////////////////wAAAAD///////////////////////////+AAAAAf4//////////////////////////oAAA" +
                        "AH+f/////////////////////////8AAAABkH///////+//////////////////wAAAAAA////////v////////////" +
                        "/////4AAAAAAP///////z/////////////////+wAAAAAB///////8//////////////////8AAAAAA////////P///" +
                        "///////////////AAAAAAP///////j//////////////////wAAAAAA///////4//////////////////6AAAAAAH//" +
                        "////8P//////////////////4AAAAAH///////j//////////////////4AAAAAA///////Af/////////////////+" +
                        "AAAAAA1//////4P//////////////////AAAAAAAP/////4D//////////////////8AAAAAAF/////8Af/////////" +
                        "////////+AAAAAAAP////+AD//////////////////AAAAAAAC////+QD///////////////////wAAAAAA/////AAP" +
                        "//////////////////gAAAAAAP///+QAD//////////////////4AAAAAAX9///AAB//////////////////4AAAAAA" +
                        "B+L/9wAAD/////////////////+AAAAAAAPxhzMgAA//////////////////gAAAAAAHoA8AAAAf///////////////" +
                        "//8AAAAAAAAAHgAAAP/////////////////8AAAAAAAIAAgYAAPv////////////////+AAAAAAAAAAEAAAAD//////" +
                        "///////////wAAAAAAAAAAAAAAFf////////////////6AAAAAAAAAAAAAAAH/////////////////wAAAAAAAAAAAA" +
                        "AAH/////////////////+AAAAAAAAAAAAAAB//////////////////4AAAAAAAAAAAAAB//////////////////4AAA" +
                        "AAAAAAAAAAAKf////////////////cAAAAAAAAAAAAAAAH////////////////0AAAAAAAAAAAAAAARf///////////" +
                        "////4QAAAAAAAAAAAAAAAH///////////////8AAAAAAAAAAAAAAAAB///////////////+gAAAAAAAAAAAAAAAA///" +
                        "/////////////4AAAAAAAAAAAAAAAAv///////////////3AAAAAAAAAAAAAAAAj///////////////9AAAAAAAAAAA" +
                        "AAAAAJ////////////////AAAAAAAAAAAAAAAAB/f//////////////YAAAAAAAAAAAAAAAARy//////////////oAA" +
                        "AAAAAAAAAAAAAAgAf/////////////8AAAAAAAAAAAAAAAAAAP/////////////8AAAAAAAAAAAAAAAAAADP///////" +
                        "//////6AAAAAAAAAAAAAAAAAAH//////////////AAAAAAAAAAAAAAAAAAAP/////////////+AAAAAAAAAAAAAAAAA" +
                        "AA//////////////AAAAAAAAAAAAAAAAAAB////////////7/gAAAAAAAAAAAAAAAAAB////////////9/4AAAAAAAA" +
                        "AAAAAAAAAA////////////+P+AAAAAAAAAAAAAAAAAAf/f/////////+h/wAAAAAAAAAAAAAAAAAE/7//////////gf" +
                        "uAAAAAAAAAAAAAAAAAAP+X/////////YBiQAAAAAAAAAAAAAAAAAB/B/////////wAAAAAAAAAAAAAAAAAAAAC/w2//" +
                        "//////wAAAAAAAAAAAAAAAAAAAAAsYA////////eABAAAAAAAAAAAAAAAAAAAFCAf///////nwAAAAAAAAAAAAAAAAA" +
                        "AAAAAgF6//////p4AAAAAAAAAAAAAAAAAAAAAAAAaM////+wCAAAAAAAAAAAAAAAAAAAAAAAAkGP///1gAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAwAf+ARggAAAAAAAAAAAAAAAAAAAAAEAAQAAf/4AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAH//AAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAD//4AAAAAAAAAAAAAAAAAAAAAAAAAAAAAB//+AAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAA///8AAAAAAAAAAAAAAAAAAAAAAAAAAAAA///5AAAAAAAAAAAAAAAAAAAAAAAAAAAAAL///AAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAA///8AAAAAAAAAAAAAAAAAAAAAAAAAAAAA///+AAAAAAAAAAAAAAAAAAAAAAAAAAAAAX///AAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAA///4AAAAAAAAAAAAAAAAAAAAAAAAAAAAAf//8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAB///" +
                        "gAAAAAAAAAAAAAAAAAAAAAAAAAAAAA///8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAP//0AAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAF//4AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAD/+AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAv4AAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAfAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAF8AAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAegAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAPAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA==";
        TestRunner.runBinary("mandelbrot.gcc-4", new String[]{"200"}, "", stdout, "", 0);
    }

    @Test
    public void mandelbrot_gcc8() throws Exception {
        String stdout = "UDQKMjAwIDIwMAoAAAAAAAAAAAAAAAAAAAAAAAACAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHoAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAF8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAfAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHwAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAv4AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAP/4AAAAAAAAAAAAAAAAAAAAAAAAAAAAABf" +
                        "/+AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAP//0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAD///wAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAf//4AAAAAAAAAAAAAAAAAAAAAAAAAAAAAf//8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAD///gAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAF///wAAAAAAAAAAAAAAAAAAAAAAAAAAAAA///+AAAAAAAAAAAAAAAAAAAAAAAAAAAAAD///wAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAC///wAAAAAAAAAAAAAAAAAAAAAAAAAAAAA///5AAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AD///wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAf//gAAAAAAAAAAAAAAAAAAAAAAAAAAAAAD//4AAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAf/8AAAAAAAAAAAAAAAAAAAAAAAABAAEAAH/+AAAAAAAAAAAAAAAAAAAAAAAAAAAAAwAf+ARggAAAAAAA" +
                        "AAAAAAAAAAAAAAAACQY////WAAAAAAAAAAAAAAAAAAAAAAAAAAGjP////sAgAAAAAAAAAAAAAAAAAAAAAAgF6//////" +
                        "p4AAAAAAAAAAAAAAAAAAAAAUIB///////+fAAAAAAAAAAAAAAAAAAAAALGAP///////3gAQAAAAAAAAAAAAAAAAAAC/" +
                        "w2////////wAAAAAAAAAAAAAAAAAAAAAH8H/////////AAAAAAAAAAAAAAAAAAAAAD/l/////////2AYkAAAAAAAAAA" +
                        "AAAAAAAE/7//////////gfuAAAAAAAAAAAAAAAAAB/9//////////6H/AAAAAAAAAAAAAAAAAAP////////////j/gA" +
                        "AAAAAAAAAAAAAAAAB////////////9/4AAAAAAAAAAAAAAAAAAH////////////v+AAAAAAAAAAAAAAAAAAAP//////" +
                        "///////wAAAAAAAAAAAAAAAAAAP/////////////+AAAAAAAAAAAAAAAAAAf/////////////8AAAAAAAAAAAAAAAAA" +
                        "Az/////////////+gAAAAAAAAAAAAAAAAAP/////////////8AAAAAAAAAAAAAAAACAB//////////////wAAAAAAAA" +
                        "AAAAAAAAEcv/////////////6AAAAAAAAAAAAAAAAB/f//////////////YAAAAAAAAAAAAAAAAn///////////////" +
                        "8AAAAAAAAAAAAAAAAI////////////////QAAAAAAAAAAAAAAAAv///////////////3AAAAAAAAAAAAAAAAD//////" +
                        "//////////gAAAAAAAAAAAAAAAAf///////////////oAAAAAAAAAAAAAAAAH///////////////8AAAAAAAAAAAAAA" +
                        "ABF////////////////hAAAAAAAAAAAAAAAB////////////////9AAAAAAAAAAAAAAAKf////////////////cAAAA" +
                        "AAAAAAAAAAH//////////////////gAAAAAAAAAAAAAAf/////////////////+AAAAAAAAAAAAAAH/////////////" +
                        "////+AAAAAAAAAAAAAAAf/////////////////AAAAAAAAAAAAAABX////////////////+gAAAAAAAAAEAAAAD////" +
                        "/////////////wAAAAAAAgACBgAA+/////////////////4AAAAAAAAAB4AAAD//////////////////AAAAAAAHoA8" +
                        "AAAAf/////////////////8AAAAAAA/GHMyAAD/////////////////+AAAAAAAfi//cAAA//////////////////gA" +
                        "AAAAAX9///AAB//////////////////4AAAAAAA////5AAP//////////////////gAAAAAAP////wAD///////////" +
                        "///////4AAAAAAC////+QD///////////////////wAAAAAA/////4AP/////////////////8AAAAAABf/////AH//" +
                        "////////////////gAAAAAAP/////4D//////////////////8AAAAADX//////g//////////////////8AAAAAAP/" +
                        "/////wH//////////////////gAAAAAH///////j//////////////////4AAAAAAf//////w//////////////////" +
                        "/gAAAAAP//////+P/////////////////+gAAAAAP///////j//////////////////wAAAAAD///////8/////////" +
                        "/////////8AAAAAAf///////P//////////////////AAAAAAP///////z/////////////////+wAAAAAD///////+" +
                        "//////////////////gAAAAZB////////v/////////////////8AAAAH+f/////////////////////////8AAAAB/" +
                        "j/////////////////////////+gAAAA////////////////////////////gAAAAP/////////////////////////" +
                        "//wAAAAH///////////////////////////wAAAAH///////////////////////////wAAA///////////////////" +
                        "//////////wAAAAAf///////////////////////////AAAAAB///////////////////////////8AAAAAP///////" +
                        "////////////////////wAAAAD///////////////////////////+AAAAAf4//////////////////////////oAAA" +
                        "AH+f/////////////////////////8AAAABkH///////+//////////////////wAAAAAA////////v////////////" +
                        "/////4AAAAAAP///////z/////////////////+wAAAAAB///////8//////////////////8AAAAAA////////P///" +
                        "///////////////AAAAAAP///////j//////////////////wAAAAAA///////4//////////////////6AAAAAAH//" +
                        "////8P//////////////////4AAAAAH///////j//////////////////4AAAAAA///////Af/////////////////+" +
                        "AAAAAA1//////4P//////////////////AAAAAAAP/////4D//////////////////8AAAAAAF/////8Af/////////" +
                        "////////+AAAAAAAP////+AD//////////////////AAAAAAAC////+QD///////////////////wAAAAAA/////AAP" +
                        "//////////////////gAAAAAAP///+QAD//////////////////4AAAAAAX9///AAB//////////////////4AAAAAA" +
                        "B+L/9wAAD/////////////////+AAAAAAAPxhzMgAA//////////////////gAAAAAAHoA8AAAAf///////////////" +
                        "//8AAAAAAAAAHgAAAP/////////////////8AAAAAAAIAAgYAAPv////////////////+AAAAAAAAAAEAAAAD//////" +
                        "///////////wAAAAAAAAAAAAAAFf////////////////6AAAAAAAAAAAAAAAH/////////////////wAAAAAAAAAAAA" +
                        "AAH/////////////////+AAAAAAAAAAAAAAB//////////////////4AAAAAAAAAAAAAB//////////////////4AAA" +
                        "AAAAAAAAAAAKf////////////////cAAAAAAAAAAAAAAAH////////////////0AAAAAAAAAAAAAAARf///////////" +
                        "////4QAAAAAAAAAAAAAAAH///////////////8AAAAAAAAAAAAAAAAB///////////////+gAAAAAAAAAAAAAAAA///" +
                        "/////////////4AAAAAAAAAAAAAAAAv///////////////3AAAAAAAAAAAAAAAAj///////////////9AAAAAAAAAAA" +
                        "AAAAAJ////////////////AAAAAAAAAAAAAAAAB/f//////////////YAAAAAAAAAAAAAAAARy//////////////oAA" +
                        "AAAAAAAAAAAAAAgAf/////////////8AAAAAAAAAAAAAAAAAAP/////////////8AAAAAAAAAAAAAAAAAADP///////" +
                        "//////6AAAAAAAAAAAAAAAAAAH//////////////AAAAAAAAAAAAAAAAAAAP/////////////+AAAAAAAAAAAAAAAAA" +
                        "AA//////////////AAAAAAAAAAAAAAAAAAB////////////7/gAAAAAAAAAAAAAAAAAB////////////9/4AAAAAAAA" +
                        "AAAAAAAAAA////////////+P+AAAAAAAAAAAAAAAAAAf/f/////////+h/wAAAAAAAAAAAAAAAAAE/7//////////gf" +
                        "uAAAAAAAAAAAAAAAAAAP+X/////////YBiQAAAAAAAAAAAAAAAAAB/B/////////wAAAAAAAAAAAAAAAAAAAAC/w2//" +
                        "//////wAAAAAAAAAAAAAAAAAAAAAsYA////////eABAAAAAAAAAAAAAAAAAAAFCAf///////nwAAAAAAAAAAAAAAAAA" +
                        "AAAAAgF6//////p4AAAAAAAAAAAAAAAAAAAAAAAAaM////+wCAAAAAAAAAAAAAAAAAAAAAAAAkGP///1gAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAwAf+ARggAAAAAAAAAAAAAAAAAAAAAEAAQAAf/4AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAH//AAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAD//4AAAAAAAAAAAAAAAAAAAAAAAAAAAAAB//+AAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAA///8AAAAAAAAAAAAAAAAAAAAAAAAAAAAA///5AAAAAAAAAAAAAAAAAAAAAAAAAAAAAL///AAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAA///8AAAAAAAAAAAAAAAAAAAAAAAAAAAAA///+AAAAAAAAAAAAAAAAAAAAAAAAAAAAAX///AAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAA///4AAAAAAAAAAAAAAAAAAAAAAAAAAAAAf//8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAB///" +
                        "gAAAAAAAAAAAAAAAAAAAAAAAAAAAAA///8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAP//0AAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAF//4AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAD/+AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAv4AAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAfAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAF8AAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAegAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAPAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA==";
        TestRunner.runBinary("mandelbrot.gcc-8", new String[]{"200"}, "", stdout, "", 0);
    }

    @Test
    public void mandelbrot_gcc9() throws Exception {
        String stdout = "UDQKMjAwIDIwMAoAAAAAAAAAAAAAAAAAAAAAAAACAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHoAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAF8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAfAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHwAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAv4AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAP/4AAAAAAAAAAAAAAAAAAAAAAAAAAAAABf" +
                        "/+AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAP//0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAD///wAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAf//4AAAAAAAAAAAAAAAAAAAAAAAAAAAAAf//8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAD///gAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAF///wAAAAAAAAAAAAAAAAAAAAAAAAAAAAA///+AAAAAAAAAAAAAAAAAAAAAAAAAAAAAD///wAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAC///wAAAAAAAAAAAAAAAAAAAAAAAAAAAAA///5AAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AD///wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAf//gAAAAAAAAAAAAAAAAAAAAAAAAAAAAAD//4AAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAf/8AAAAAAAAAAAAAAAAAAAAAAAABAAEAAH/+AAAAAAAAAAAAAAAAAAAAAAAAAAAAAwAf+ARggAAAAAAA" +
                        "AAAAAAAAAAAAAAAACQY////WAAAAAAAAAAAAAAAAAAAAAAAAAAGjP////sAgAAAAAAAAAAAAAAAAAAAAAAgF6//////" +
                        "p4AAAAAAAAAAAAAAAAAAAAAUIB///////+fAAAAAAAAAAAAAAAAAAAAALGAP///////3gAQAAAAAAAAAAAAAAAAAAC/" +
                        "w2////////wAAAAAAAAAAAAAAAAAAAAAH8H/////////AAAAAAAAAAAAAAAAAAAAAD/l/////////2AYkAAAAAAAAAA" +
                        "AAAAAAAE/7//////////gfuAAAAAAAAAAAAAAAAAB/9//////////6H/AAAAAAAAAAAAAAAAAAP////////////j/gA" +
                        "AAAAAAAAAAAAAAAAB////////////9/4AAAAAAAAAAAAAAAAAAH////////////v+AAAAAAAAAAAAAAAAAAAP//////" +
                        "///////wAAAAAAAAAAAAAAAAAAP/////////////+AAAAAAAAAAAAAAAAAAf/////////////8AAAAAAAAAAAAAAAAA" +
                        "Az/////////////+gAAAAAAAAAAAAAAAAAP/////////////8AAAAAAAAAAAAAAAACAB//////////////wAAAAAAAA" +
                        "AAAAAAAAEcv/////////////6AAAAAAAAAAAAAAAAB/f//////////////YAAAAAAAAAAAAAAAAn///////////////" +
                        "8AAAAAAAAAAAAAAAAI////////////////QAAAAAAAAAAAAAAAAv///////////////3AAAAAAAAAAAAAAAAD//////" +
                        "//////////gAAAAAAAAAAAAAAAAf///////////////oAAAAAAAAAAAAAAAAH///////////////8AAAAAAAAAAAAAA" +
                        "ABF////////////////hAAAAAAAAAAAAAAAB////////////////9AAAAAAAAAAAAAAAKf////////////////cAAAA" +
                        "AAAAAAAAAAH//////////////////gAAAAAAAAAAAAAAf/////////////////+AAAAAAAAAAAAAAH/////////////" +
                        "////+AAAAAAAAAAAAAAAf/////////////////AAAAAAAAAAAAAABX////////////////+gAAAAAAAAAEAAAAD////" +
                        "/////////////wAAAAAAAgACBgAA+/////////////////4AAAAAAAAAB4AAAD//////////////////AAAAAAAHoA8" +
                        "AAAAf/////////////////8AAAAAAA/GHMyAAD/////////////////+AAAAAAAfi//cAAA//////////////////gA" +
                        "AAAAAX9///AAB//////////////////4AAAAAAA////5AAP//////////////////gAAAAAAP////wAD///////////" +
                        "///////4AAAAAAC////+QD///////////////////wAAAAAA/////4AP/////////////////8AAAAAABf/////AH//" +
                        "////////////////gAAAAAAP/////4D//////////////////8AAAAADX//////g//////////////////8AAAAAAP/" +
                        "/////wH//////////////////gAAAAAH///////j//////////////////4AAAAAAf//////w//////////////////" +
                        "/gAAAAAP//////+P/////////////////+gAAAAAP///////j//////////////////wAAAAAD///////8/////////" +
                        "/////////8AAAAAAf///////P//////////////////AAAAAAP///////z/////////////////+wAAAAAD///////+" +
                        "//////////////////gAAAAZB////////v/////////////////8AAAAH+f/////////////////////////8AAAAB/" +
                        "j/////////////////////////+gAAAA////////////////////////////gAAAAP/////////////////////////" +
                        "//wAAAAH///////////////////////////wAAAAH///////////////////////////wAAA///////////////////" +
                        "//////////wAAAAAf///////////////////////////AAAAAB///////////////////////////8AAAAAP///////" +
                        "////////////////////wAAAAD///////////////////////////+AAAAAf4//////////////////////////oAAA" +
                        "AH+f/////////////////////////8AAAABkH///////+//////////////////wAAAAAA////////v////////////" +
                        "/////4AAAAAAP///////z/////////////////+wAAAAAB///////8//////////////////8AAAAAA////////P///" +
                        "///////////////AAAAAAP///////j//////////////////wAAAAAA///////4//////////////////6AAAAAAH//" +
                        "////8P//////////////////4AAAAAH///////j//////////////////4AAAAAA///////Af/////////////////+" +
                        "AAAAAA1//////4P//////////////////AAAAAAAP/////4D//////////////////8AAAAAAF/////8Af/////////" +
                        "////////+AAAAAAAP////+AD//////////////////AAAAAAAC////+QD///////////////////wAAAAAA/////AAP" +
                        "//////////////////gAAAAAAP///+QAD//////////////////4AAAAAAX9///AAB//////////////////4AAAAAA" +
                        "B+L/9wAAD/////////////////+AAAAAAAPxhzMgAA//////////////////gAAAAAAHoA8AAAAf///////////////" +
                        "//8AAAAAAAAAHgAAAP/////////////////8AAAAAAAIAAgYAAPv////////////////+AAAAAAAAAAEAAAAD//////" +
                        "///////////wAAAAAAAAAAAAAAFf////////////////6AAAAAAAAAAAAAAAH/////////////////wAAAAAAAAAAAA" +
                        "AAH/////////////////+AAAAAAAAAAAAAAB//////////////////4AAAAAAAAAAAAAB//////////////////4AAA" +
                        "AAAAAAAAAAAKf////////////////cAAAAAAAAAAAAAAAH////////////////0AAAAAAAAAAAAAAARf///////////" +
                        "////4QAAAAAAAAAAAAAAAH///////////////8AAAAAAAAAAAAAAAAB///////////////+gAAAAAAAAAAAAAAAA///" +
                        "/////////////4AAAAAAAAAAAAAAAAv///////////////3AAAAAAAAAAAAAAAAj///////////////9AAAAAAAAAAA" +
                        "AAAAAJ////////////////AAAAAAAAAAAAAAAAB/f//////////////YAAAAAAAAAAAAAAAARy//////////////oAA" +
                        "AAAAAAAAAAAAAAgAf/////////////8AAAAAAAAAAAAAAAAAAP/////////////8AAAAAAAAAAAAAAAAAADP///////" +
                        "//////6AAAAAAAAAAAAAAAAAAH//////////////AAAAAAAAAAAAAAAAAAAP/////////////+AAAAAAAAAAAAAAAAA" +
                        "AA//////////////AAAAAAAAAAAAAAAAAAB////////////7/gAAAAAAAAAAAAAAAAAB////////////9/4AAAAAAAA" +
                        "AAAAAAAAAA////////////+P+AAAAAAAAAAAAAAAAAAf/f/////////+h/wAAAAAAAAAAAAAAAAAE/7//////////gf" +
                        "uAAAAAAAAAAAAAAAAAAP+X/////////YBiQAAAAAAAAAAAAAAAAAB/B/////////wAAAAAAAAAAAAAAAAAAAAC/w2//" +
                        "//////wAAAAAAAAAAAAAAAAAAAAAsYA////////eABAAAAAAAAAAAAAAAAAAAFCAf///////nwAAAAAAAAAAAAAAAAA" +
                        "AAAAAgF6//////p4AAAAAAAAAAAAAAAAAAAAAAAAaM////+wCAAAAAAAAAAAAAAAAAAAAAAAAkGP///1gAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAwAf+ARggAAAAAAAAAAAAAAAAAAAAAEAAQAAf/4AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAH//AAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAD//4AAAAAAAAAAAAAAAAAAAAAAAAAAAAAB//+AAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAA///8AAAAAAAAAAAAAAAAAAAAAAAAAAAAA///5AAAAAAAAAAAAAAAAAAAAAAAAAAAAAL///AAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAA///8AAAAAAAAAAAAAAAAAAAAAAAAAAAAA///+AAAAAAAAAAAAAAAAAAAAAAAAAAAAAX///AAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAA///4AAAAAAAAAAAAAAAAAAAAAAAAAAAAAf//8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAB///" +
                        "gAAAAAAAAAAAAAAAAAAAAAAAAAAAAA///8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAP//0AAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAF//4AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAD/+AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAv4AAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAfAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAF8AAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAegAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAPAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA==";
        TestRunner.runBinary("mandelbrot.gcc-9", new String[]{"200"}, "", stdout, "", 0);
    }

    @Test
    public void binarytrees_gcc() throws Exception {
        String stdout = "stretch tree of depth 11\t check: -1\n" +
                        "2048\t trees of depth 4\t check: -2048\n" +
                        "512\t trees of depth 6\t check: -512\n" +
                        "128\t trees of depth 8\t check: -128\n" +
                        "32\t trees of depth 10\t check: -32\n" +
                        "long lived tree of depth 10\t check: -1\n";
        TestRunner.run("binarytrees.gcc", new String[]{"10"}, "", stdout, "", 0);
    }

    @Test
    public void binarytrees_gcc2() throws Exception {
        String stdout = "stretch tree of depth 11\t check: -1\n" +
                        "2048\t trees of depth 4\t check: -2048\n" +
                        "512\t trees of depth 6\t check: -512\n" +
                        "128\t trees of depth 8\t check: -128\n" +
                        "32\t trees of depth 10\t check: -32\n" +
                        "long lived tree of depth 10\t check: -1\n";
        TestRunner.run("binarytrees.gcc-2", new String[]{"10"}, "", stdout, "", 0);
    }
}
