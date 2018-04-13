package org.graalvm.vm.x86.test.runner;

import org.junit.Ignore;
import org.junit.Test;

public class BenchmarksGame {
    public static final String FASTA = ">ONE Homo sapiens alu\n" +
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

    public static final String FASTAREDUX = ">ONE Homo sapiens alu\n" +
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

    public static final String MANDELBROT = "UDQKMjAwIDIwMAoAAAAAAAAAAAAAAAAAAAAAAAACAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA8AAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAHoAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAF8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAfAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAHwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAv4AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAP/4AAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAABf/+AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAP//0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAD/" +
                    "//wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAf//4AAAAAAAAAAAAAAAAAAAAAAAAAAAAAf//8AAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAD///gAAAAAAAAAAAAAAAAAAAAAAAAAAAAF///wAAAAAAAAAAAAAAAAAAAAAAAAAAAAA///+AAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAD///wAAAAAAAAAAAAAAAAAAAAAAAAAAAAC///wAAAAAAAAAAAAAAAAAAAAAAAAAAAAA///5AAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAD///wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAf//gAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AD//4AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAf/8AAAAAAAAAAAAAAAAAAAAAAAABAAEAAH/+AAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAwAf+ARggAAAAAAAAAAAAAAAAAAAAAAACQY////WAAAAAAAAAAAAAAAAAAAAAAAAAAGjP////sAgAAAAAAA" +
                    "AAAAAAAAAAAAAAAgF6//////p4AAAAAAAAAAAAAAAAAAAAAUIB///////+fAAAAAAAAAAAAAAAAAAAAALGAP///////" +
                    "3gAQAAAAAAAAAAAAAAAAAAC/w2////////wAAAAAAAAAAAAAAAAAAAAAH8H/////////AAAAAAAAAAAAAAAAAAAAAD/" +
                    "l/////////2AYkAAAAAAAAAAAAAAAAAE/7//////////gfuAAAAAAAAAAAAAAAAAB/9//////////6H/AAAAAAAAAAA" +
                    "AAAAAAAP////////////j/gAAAAAAAAAAAAAAAAAB////////////9/4AAAAAAAAAAAAAAAAAAH////////////v+AA" +
                    "AAAAAAAAAAAAAAAAAP/////////////wAAAAAAAAAAAAAAAAAAP/////////////+AAAAAAAAAAAAAAAAAAf///////" +
                    "//////8AAAAAAAAAAAAAAAAAAz/////////////+gAAAAAAAAAAAAAAAAAP/////////////8AAAAAAAAAAAAAAAACA" +
                    "B//////////////wAAAAAAAAAAAAAAAAEcv/////////////6AAAAAAAAAAAAAAAAB/f//////////////YAAAAAAAA" +
                    "AAAAAAAAn///////////////8AAAAAAAAAAAAAAAAI////////////////QAAAAAAAAAAAAAAAAv///////////////" +
                    "3AAAAAAAAAAAAAAAAD////////////////gAAAAAAAAAAAAAAAAf///////////////oAAAAAAAAAAAAAAAAH//////" +
                    "/////////8AAAAAAAAAAAAAAABF////////////////hAAAAAAAAAAAAAAAB////////////////9AAAAAAAAAAAAAA" +
                    "AKf////////////////cAAAAAAAAAAAAAAH//////////////////gAAAAAAAAAAAAAAf/////////////////+AAAA" +
                    "AAAAAAAAAAH/////////////////+AAAAAAAAAAAAAAAf/////////////////AAAAAAAAAAAAAABX/////////////" +
                    "///+gAAAAAAAAAEAAAAD/////////////////wAAAAAAAgACBgAA+/////////////////4AAAAAAAAAB4AAAD/////" +
                    "/////////////AAAAAAAHoA8AAAAf/////////////////8AAAAAAA/GHMyAAD/////////////////+AAAAAAAfi//" +
                    "cAAA//////////////////gAAAAAAX9///AAB//////////////////4AAAAAAA////5AAP//////////////////gA" +
                    "AAAAAP////wAD//////////////////4AAAAAAC////+QD///////////////////wAAAAAA/////4AP///////////" +
                    "//////8AAAAAABf/////AH//////////////////gAAAAAAP/////4D//////////////////8AAAAADX//////g///" +
                    "///////////////8AAAAAAP//////wH//////////////////gAAAAAH///////j//////////////////4AAAAAAf/" +
                    "/////w///////////////////gAAAAAP//////+P/////////////////+gAAAAAP///////j//////////////////" +
                    "wAAAAAD///////8//////////////////8AAAAAAf///////P//////////////////AAAAAAP///////z/////////" +
                    "////////+wAAAAAD///////+//////////////////gAAAAZB////////v/////////////////8AAAAH+f////////" +
                    "/////////////////8AAAAB/j/////////////////////////+gAAAA////////////////////////////gAAAAP/" +
                    "//////////////////////////wAAAAH///////////////////////////wAAAAH//////////////////////////" +
                    "/wAAA/////////////////////////////wAAAAAf///////////////////////////AAAAAB/////////////////" +
                    "//////////8AAAAAP///////////////////////////wAAAAD///////////////////////////+AAAAAf4//////" +
                    "////////////////////oAAAAH+f/////////////////////////8AAAABkH///////+//////////////////wAAA" +
                    "AAA////////v/////////////////4AAAAAAP///////z/////////////////+wAAAAAB///////8/////////////" +
                    "/////8AAAAAA////////P//////////////////AAAAAAP///////j//////////////////wAAAAAA///////4////" +
                    "//////////////6AAAAAAH//////8P//////////////////4AAAAAH///////j//////////////////4AAAAAA///" +
                    "////Af/////////////////+AAAAAA1//////4P//////////////////AAAAAAAP/////4D//////////////////8" +
                    "AAAAAAF/////8Af/////////////////+AAAAAAAP////+AD//////////////////AAAAAAAC////+QD//////////" +
                    "/////////wAAAAAA/////AAP//////////////////gAAAAAAP///+QAD//////////////////4AAAAAAX9///AAB/" +
                    "/////////////////4AAAAAAB+L/9wAAD/////////////////+AAAAAAAPxhzMgAA//////////////////gAAAAAA" +
                    "HoA8AAAAf/////////////////8AAAAAAAAAHgAAAP/////////////////8AAAAAAAIAAgYAAPv///////////////" +
                    "/+AAAAAAAAAAEAAAAD/////////////////wAAAAAAAAAAAAAAFf////////////////6AAAAAAAAAAAAAAAH//////" +
                    "///////////wAAAAAAAAAAAAAAH/////////////////+AAAAAAAAAAAAAAB//////////////////4AAAAAAAAAAAA" +
                    "AB//////////////////4AAAAAAAAAAAAAAKf////////////////cAAAAAAAAAAAAAAAH////////////////0AAAA" +
                    "AAAAAAAAAAARf///////////////4QAAAAAAAAAAAAAAAH///////////////8AAAAAAAAAAAAAAAAB////////////" +
                    "///+gAAAAAAAAAAAAAAAA////////////////4AAAAAAAAAAAAAAAAv///////////////3AAAAAAAAAAAAAAAAj///" +
                    "////////////9AAAAAAAAAAAAAAAAJ////////////////AAAAAAAAAAAAAAAAB/f//////////////YAAAAAAAAAAA" +
                    "AAAAARy//////////////oAAAAAAAAAAAAAAAAgAf/////////////8AAAAAAAAAAAAAAAAAAP/////////////8AAA" +
                    "AAAAAAAAAAAAAAADP/////////////6AAAAAAAAAAAAAAAAAAH//////////////AAAAAAAAAAAAAAAAAAAP///////" +
                    "//////+AAAAAAAAAAAAAAAAAAA//////////////AAAAAAAAAAAAAAAAAAB////////////7/gAAAAAAAAAAAAAAAAA" +
                    "B////////////9/4AAAAAAAAAAAAAAAAAA////////////+P+AAAAAAAAAAAAAAAAAAf/f/////////+h/wAAAAAAAA" +
                    "AAAAAAAAAE/7//////////gfuAAAAAAAAAAAAAAAAAAP+X/////////YBiQAAAAAAAAAAAAAAAAAB/B/////////wAA" +
                    "AAAAAAAAAAAAAAAAAAC/w2////////wAAAAAAAAAAAAAAAAAAAAAsYA////////eABAAAAAAAAAAAAAAAAAAAFCAf//" +
                    "/////nwAAAAAAAAAAAAAAAAAAAAAAgF6//////p4AAAAAAAAAAAAAAAAAAAAAAAAaM////+wCAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAkGP///1gAAAAAAAAAAAAAAAAAAAAAAAAAAAwAf+ARggAAAAAAAAAAAAAAAAAAAAAEAAQAAf/4AAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAH//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAD//4AAAAAAAAAAAAAAAAAAAAAAAAAAAAAB//+AAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAA///8AAAAAAAAAAAAAAAAAAAAAAAAAAAAA///5AAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAL///AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA///8AAAAAAAAAAAAAAAAAAAAAAAAAAAAA///+AAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAX///AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA///4AAAAAAAAAAAAAAAAAAAAAAAAAAAAAf//8AAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAB///gAAAAAAAAAAAAAAAAAAAAAAAAAAAAA///8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAP//0" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAF//4AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAD/+AAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAv4AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAfAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHwAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAF8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAegAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAPAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA==";

    @Test
    public void fasta_cint() throws Exception {
        TestRunner.run("fasta.cint", new String[]{"1000"}, "", FASTA, "", 0);
    }

    @Test
    public void fasta_gcc() throws Exception {
        TestRunner.run("fasta.gcc", new String[0], "", FASTA, "", 0);
    }

    @Test
    public void fasta_gcc4() throws Exception {
        TestRunner.run("fasta.gcc-4", new String[0], "", FASTA, "", 0);
    }

    @Test
    public void fastaredux_gcc2() throws Exception {
        TestRunner.run("fastaredux.gcc-2", new String[]{"1000"}, "", FASTAREDUX, "", 0);
    }

    @Test
    public void fastaredux_gcc3() throws Exception {
        TestRunner.run("fastaredux.gcc-3", new String[]{"1000"}, "", FASTAREDUX, "", 0);
    }

    @Ignore("interpreter bug")
    @Test
    public void fastaredux_gcc5() throws Exception {
        TestRunner.run("fastaredux.gcc-5", new String[]{"1000"}, "", FASTAREDUX, "", 0);
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
        TestRunner.runBinary("mandelbrot.cint", new String[]{"200"}, "", MANDELBROT, "", 0);
    }

    @Test
    public void mandelbrot_gcc2() throws Exception {
        TestRunner.runBinary("mandelbrot.gcc-2", new String[]{"200"}, "", MANDELBROT, "", 0);
    }

    @Test
    public void mandelbrot_gcc4() throws Exception {
        TestRunner.runBinary("mandelbrot.gcc-4", new String[]{"200"}, "", MANDELBROT, "", 0);
    }

    @Test
    public void mandelbrot_gcc8() throws Exception {
        TestRunner.runBinary("mandelbrot.gcc-8", new String[]{"200"}, "", MANDELBROT, "", 0);
    }

    @Test
    public void mandelbrot_gcc9() throws Exception {
        TestRunner.runBinary("mandelbrot.gcc-9", new String[]{"200"}, "", MANDELBROT, "", 0);
    }

    @Ignore("interpreter bug")
    @Test
    public void mandelbrot_gpp() throws Exception {
        TestRunner.runBinary("mandelbrot.gpp", new String[]{"200"}, "", MANDELBROT, "", 0);
    }

    @Test
    public void mandelbrot_gpp2() throws Exception {
        TestRunner.runBinary("mandelbrot.gpp-2", new String[]{"200"}, "", MANDELBROT, "", 0);
    }

    @Test
    public void mandelbrot_gpp3() throws Exception {
        TestRunner.runBinary("mandelbrot.gpp-3", new String[]{"200"}, "", MANDELBROT, "", 0);
    }

    @Test
    public void mandelbrot_gpp5() throws Exception {
        TestRunner.runBinary("mandelbrot.gpp-5", new String[]{"200"}, "", MANDELBROT, "", 0);
    }

    @Test
    public void mandelbrot_gpp6() throws Exception {
        TestRunner.runBinary("mandelbrot.gpp-6", new String[]{"200"}, "", MANDELBROT, "", 0);
    }

    @Test
    public void mandelbrot_gpp7() throws Exception {
        TestRunner.runBinary("mandelbrot.gpp-7", new String[]{"200"}, "", MANDELBROT, "", 0);
    }

    @Ignore("interpreter bug")
    @Test
    public void mandelbrot_gpp8() throws Exception {
        TestRunner.runBinary("mandelbrot.gpp-8", new String[]{"200"}, "", MANDELBROT, "", 0);
    }

    @Ignore("interpreter bug")
    @Test
    public void mandelbrot_gpp9() throws Exception {
        TestRunner.runBinary("mandelbrot.gpp-9", new String[]{"200"}, "", MANDELBROT, "", 0);
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
