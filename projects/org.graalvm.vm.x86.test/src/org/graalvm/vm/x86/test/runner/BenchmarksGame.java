package org.graalvm.vm.x86.test.runner;

import static org.junit.Assume.assumeTrue;

import org.graalvm.vm.x86.test.platform.HostTest;
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

    public static final String MANDELBROT_GPP = "UDQKMjAwIDIwMAoAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA8AAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAHoAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAF8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAfAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAHwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAv4AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAP/4AAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAABf/+AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAP//0AAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAD///wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAf//4AAAAAAAAAAAAAAAAAAAAAAAAAAAAAf//8AAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAD///gAAAAAAAAAAAAAAAAAAAAAAAAAAAAF///wAAAAAAAAAAAAAAAAAAAAAAAAAAAAA///+AAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAD///wAAAAAAAAAAAAAAAAAAAAAAAAAAAAC///wAAAAAAAAAAAAAAAAAAAAAAAAAAAAA///5" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAD///wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAf//gAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAD//4AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAf/8AAAAAAAAAAAAAAAAAAAAAAAABAAEAAH/+AAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAwAf+ARggAAAAAAAAAAAAAAAAAAAAAAACQY////WAAAAAAAAAAAAAAAAAAAAAAAAAAGjP////sAgAAA" +
                    "AAAAAAAAAAAAAAAAAAAgF6//////p4AAAAAAAAAAAAAAAAAAAAAUIB///////+fAAAAAAAAAAAAAAAAAAAAALGAP///" +
                    "////3gAQAAAAAAAAAAAAAAAAAAC/w2////////wAAAAAAAAAAAAAAAAAAAAAH8H/////////AAAAAAAAAAAAAAAAAAA" +
                    "AAD/l/////////2AYkAAAAAAAAAAAAAAAAAE/7//////////gfuAAAAAAAAAAAAAAAAAB/9//////////6H/AAAAAAA" +
                    "AAAAAAAAAAAP////////////j/gAAAAAAAAAAAAAAAAAB////////////9/4AAAAAAAAAAAAAAAAAAH////////////" +
                    "v+AAAAAAAAAAAAAAAAAAAP/////////////wAAAAAAAAAAAAAAAAAAP/////////////+AAAAAAAAAAAAAAAAAAf///" +
                    "//////////8AAAAAAAAAAAAAAAAAAz/////////////+gAAAAAAAAAAAAAAAAAP/////////////8AAAAAAAAAAAAAA" +
                    "AACAB//////////////wAAAAAAAAAAAAAAAAEcv/////////////6AAAAAAAAAAAAAAAAB/f//////////////YAAAA" +
                    "AAAAAAAAAAAAn///////////////8AAAAAAAAAAAAAAAAI////////////////QAAAAAAAAAAAAAAAAv///////////" +
                    "////3AAAAAAAAAAAAAAAAD////////////////gAAAAAAAAAAAAAAAAf///////////////oAAAAAAAAAAAAAAAAH//" +
                    "/////////////8AAAAAAAAAAAAAAABF////////////////hAAAAAAAAAAAAAAAB////////////////9AAAAAAAAAA" +
                    "AAAAAKf////////////////cAAAAAAAAAAAAAAH//////////////////gAAAAAAAAAAAAAAf/////////////////+" +
                    "AAAAAAAAAAAAAAH/////////////////+AAAAAAAAAAAAAAAf/////////////////AAAAAAAAAAAAAABX/////////" +
                    "///////+gAAAAAAAAAEAAAAD/////////////////wAAAAAAAgACBgAA+/////////////////4AAAAAAAAAB4AAAD/" +
                    "/////////////////AAAAAAAHoA8AAAAf/////////////////8AAAAAAA/GHMyAAD/////////////////+AAAAAAA" +
                    "fi//cAAA//////////////////gAAAAAAX9///AAB//////////////////4AAAAAAA////5AAP////////////////" +
                    "//gAAAAAAP////wAD//////////////////4AAAAAAC////+QD///////////////////wAAAAAA/////4AP///////" +
                    "//////////8AAAAAABf/////AH//////////////////gAAAAAAP/////4D//////////////////8AAAAADX//////" +
                    "g//////////////////8AAAAAAP//////wH//////////////////gAAAAAH///////j//////////////////4AAAA" +
                    "AAf//////w///////////////////gAAAAAP//////+P/////////////////+gAAAAAP///////j//////////////" +
                    "////wAAAAAD///////8//////////////////8AAAAAAf///////P//////////////////AAAAAAP///////z/////" +
                    "////////////+wAAAAAD///////+//////////////////gAAAAZB////////v/////////////////8AAAAH+f////" +
                    "/////////////////////8AAAAB/j/////////////////////////+gAAAA////////////////////////////gAA" +
                    "AAP///////////////////////////wAAAAH///////////////////////////wAAAAH//////////////////////" +
                    "/////wAAA/////////////////////////////wAAAAAf///////////////////////////AAAAAB/////////////" +
                    "//////////////8AAAAAP///////////////////////////wAAAAD///////////////////////////+AAAAAf4//" +
                    "////////////////////////oAAAAH+f/////////////////////////8AAAABkH///////+//////////////////" +
                    "wAAAAAA////////v/////////////////4AAAAAAP///////z/////////////////+wAAAAAB///////8/////////" +
                    "/////////8AAAAAA////////P//////////////////AAAAAAP///////j//////////////////wAAAAAA///////4" +
                    "//////////////////6AAAAAAH//////8P//////////////////4AAAAAH///////j//////////////////4AAAAA" +
                    "A///////Af/////////////////+AAAAAA1//////4P//////////////////AAAAAAAP/////4D///////////////" +
                    "///8AAAAAAF/////8Af/////////////////+AAAAAAAP////+AD//////////////////AAAAAAAC////+QD//////" +
                    "/////////////wAAAAAA/////AAP//////////////////gAAAAAAP///+QAD//////////////////4AAAAAAX9///" +
                    "AAB//////////////////4AAAAAAB+L/9wAAD/////////////////+AAAAAAAPxhzMgAA//////////////////gAA" +
                    "AAAAHoA8AAAAf/////////////////8AAAAAAAAAHgAAAP/////////////////8AAAAAAAIAAgYAAPv///////////" +
                    "/////+AAAAAAAAAAEAAAAD/////////////////wAAAAAAAAAAAAAAFf////////////////6AAAAAAAAAAAAAAAH//" +
                    "///////////////wAAAAAAAAAAAAAAH/////////////////+AAAAAAAAAAAAAAB//////////////////4AAAAAAAA" +
                    "AAAAAB//////////////////4AAAAAAAAAAAAAAKf////////////////cAAAAAAAAAAAAAAAH////////////////0" +
                    "AAAAAAAAAAAAAAARf///////////////4QAAAAAAAAAAAAAAAH///////////////8AAAAAAAAAAAAAAAAB////////" +
                    "///////+gAAAAAAAAAAAAAAAA////////////////4AAAAAAAAAAAAAAAAv///////////////3AAAAAAAAAAAAAAAA" +
                    "j///////////////9AAAAAAAAAAAAAAAAJ////////////////AAAAAAAAAAAAAAAAB/f//////////////YAAAAAAA" +
                    "AAAAAAAAARy//////////////oAAAAAAAAAAAAAAAAgAf/////////////8AAAAAAAAAAAAAAAAAAP/////////////" +
                    "8AAAAAAAAAAAAAAAAAADP/////////////6AAAAAAAAAAAAAAAAAAH//////////////AAAAAAAAAAAAAAAAAAAP///" +
                    "//////////+AAAAAAAAAAAAAAAAAAA//////////////AAAAAAAAAAAAAAAAAAB////////////7/gAAAAAAAAAAAAA" +
                    "AAAAB////////////9/4AAAAAAAAAAAAAAAAAA////////////+P+AAAAAAAAAAAAAAAAAAf/f/////////+h/wAAAA" +
                    "AAAAAAAAAAAAAE/7//////////gfuAAAAAAAAAAAAAAAAAAP+X/////////YBiQAAAAAAAAAAAAAAAAAB/B////////" +
                    "/wAAAAAAAAAAAAAAAAAAAAC/w2////////wAAAAAAAAAAAAAAAAAAAAAsYA////////eABAAAAAAAAAAAAAAAAAAAFC" +
                    "Af///////nwAAAAAAAAAAAAAAAAAAAAAAgF6//////p4AAAAAAAAAAAAAAAAAAAAAAAAaM////+wCAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAkGP///1gAAAAAAAAAAAAAAAAAAAAAAAAAAAwAf+ARggAAAAAAAAAAAAAAAAAAAAAEAAQAAf/4AAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAH//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAD//4AAAAAAAAAAAAAAAAAAAAAAAAAAAAAB//" +
                    "+AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA///8AAAAAAAAAAAAAAAAAAAAAAAAAAAAA///5AAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAL///AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA///8AAAAAAAAAAAAAAAAAAAAAAAAAAAAA///+AAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAX///AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA///4AAAAAAAAAAAAAAAAAAAAAAAAAAAAAf//8AAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAB///gAAAAAAAAAAAAAAAAAAAAAAAAAAAAA///8AAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "P//0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAF//4AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAD/+AAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAv4AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAfAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHwAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAF8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAegAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAPAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA==";

    public static final String PIDIGITS = "3141592653\t:10\n" +
                    "5897932384\t:20\n" +
                    "6264338327\t:30\n" +
                    "9502884197\t:40\n" +
                    "1693993751\t:50\n" +
                    "0582097494\t:60\n" +
                    "4592307816\t:70\n" +
                    "4062862089\t:80\n" +
                    "9862803482\t:90\n" +
                    "5342117067\t:100\n" +
                    "9821480865\t:110\n" +
                    "1328230664\t:120\n" +
                    "7093844609\t:130\n" +
                    "5505822317\t:140\n" +
                    "2535940812\t:150\n" +
                    "8481117450\t:160\n" +
                    "2841027019\t:170\n" +
                    "3852110555\t:180\n" +
                    "9644622948\t:190\n" +
                    "9549303819\t:200\n" +
                    "6442881097\t:210\n" +
                    "5665933446\t:220\n" +
                    "1284756482\t:230\n" +
                    "3378678316\t:240\n" +
                    "5271201909\t:250\n" +
                    "1456485669\t:260\n" +
                    "2346034861\t:270\n" +
                    "0454326648\t:280\n" +
                    "2133936072\t:290\n" +
                    "6024914127\t:300\n" +
                    "3724587006\t:310\n" +
                    "6063155881\t:320\n" +
                    "7488152092\t:330\n" +
                    "0962829254\t:340\n" +
                    "0917153643\t:350\n" +
                    "6789259036\t:360\n" +
                    "0011330530\t:370\n" +
                    "5488204665\t:380\n" +
                    "2138414695\t:390\n" +
                    "1941511609\t:400\n" +
                    "4330572703\t:410\n" +
                    "6575959195\t:420\n" +
                    "3092186117\t:430\n" +
                    "3819326117\t:440\n" +
                    "9310511854\t:450\n" +
                    "8074462379\t:460\n" +
                    "9627495673\t:470\n" +
                    "5188575272\t:480\n" +
                    "4891227938\t:490\n" +
                    "1830119491\t:500\n" +
                    "2983367336\t:510\n" +
                    "2440656643\t:520\n" +
                    "0860213949\t:530\n" +
                    "4639522473\t:540\n" +
                    "7190702179\t:550\n" +
                    "8609437027\t:560\n" +
                    "7053921717\t:570\n" +
                    "6293176752\t:580\n" +
                    "3846748184\t:590\n" +
                    "6766940513\t:600\n" +
                    "2000568127\t:610\n" +
                    "1452635608\t:620\n" +
                    "2778577134\t:630\n" +
                    "2757789609\t:640\n" +
                    "1736371787\t:650\n" +
                    "2146844090\t:660\n" +
                    "1224953430\t:670\n" +
                    "1465495853\t:680\n" +
                    "7105079227\t:690\n" +
                    "9689258923\t:700\n" +
                    "5420199561\t:710\n" +
                    "1212902196\t:720\n" +
                    "0864034418\t:730\n" +
                    "1598136297\t:740\n" +
                    "7477130996\t:750\n" +
                    "0518707211\t:760\n" +
                    "3499999983\t:770\n" +
                    "7297804995\t:780\n" +
                    "1059731732\t:790\n" +
                    "8160963185\t:800\n" +
                    "9502445945\t:810\n" +
                    "5346908302\t:820\n" +
                    "6425223082\t:830\n" +
                    "5334468503\t:840\n" +
                    "5261931188\t:850\n";

    public static final String FANNKUCH = "228\nPfannkuchen(7) = 16\n";

    public static final String BINARYTREES = "stretch tree of depth 11\t check: -1\n" +
                    "2048\t trees of depth 4\t check: -2048\n" +
                    "512\t trees of depth 6\t check: -512\n" +
                    "128\t trees of depth 8\t check: -128\n" +
                    "32\t trees of depth 10\t check: -32\n" +
                    "long lived tree of depth 10\t check: -1\n";

    public static final String NBODY = "-0.169075164\n" +
                    "-0.169023077\n";

    public static final String SPECTRALNORM = "1.274219991\n";

    public static final String METEOR = "2098 solutions found\n" +
                    "\n" +
                    "0 0 0 0 1 \n" +
                    " 2 2 2 0 1 \n" +
                    "2 6 6 1 1 \n" +
                    " 2 6 1 5 5 \n" +
                    "8 6 5 5 5 \n" +
                    " 8 6 3 3 3 \n" +
                    "4 8 8 9 3 \n" +
                    " 4 4 8 9 3 \n" +
                    "4 7 4 7 9 \n" +
                    " 7 7 7 9 9 \n" +
                    "\n" +
                    "9 9 9 9 8 \n" +
                    " 9 6 6 8 5 \n" +
                    "6 6 8 8 5 \n" +
                    " 6 8 2 5 5 \n" +
                    "7 7 7 2 5 \n" +
                    " 7 4 7 2 0 \n" +
                    "1 4 2 2 0 \n" +
                    " 1 4 4 0 3 \n" +
                    "1 4 0 0 3 \n" +
                    " 1 1 3 3 3 \n" +
                    "\n";

    public static final String KNUCLEOTIDE = "T 31.520\n" +
                    "A 29.600\n" +
                    "C 19.480\n" +
                    "G 19.400\n" +
                    "\n" +
                    "AT 9.922\n" +
                    "TT 9.602\n" +
                    "TA 9.402\n" +
                    "AA 8.402\n" +
                    "GA 6.321\n" +
                    "TC 6.301\n" +
                    "TG 6.201\n" +
                    "GT 6.041\n" +
                    "CT 5.961\n" +
                    "AG 5.841\n" +
                    "CA 5.461\n" +
                    "AC 5.441\n" +
                    "CC 4.041\n" +
                    "CG 4.021\n" +
                    "GC 3.701\n" +
                    "GG 3.341\n" +
                    "\n" +
                    "54\tGGT\n" +
                    "24\tGGTA\n" +
                    "4\tGGTATT\n" +
                    "0\tGGTATTTTAATT\n" +
                    "0\tGGTATTTTAATTTATAGT\n";

    public static final String REGEXDNA = "agggtaaa|tttaccct 1\n" +
                    "[cgt]gggtaaa|tttaccc[acg] 0\n" +
                    "a[act]ggtaaa|tttacc[agt]t 0\n" +
                    "ag[act]gtaaa|tttac[agt]ct 0\n" +
                    "agg[act]taaa|ttta[agt]cct 1\n" +
                    "aggg[acg]aaa|ttt[cgt]ccct 0\n" +
                    "agggt[cgt]aa|tt[acg]accct 0\n" +
                    "agggta[cgt]a|t[acg]taccct 0\n" +
                    "agggtaa[cgt]|[acg]ttaccct 2\n" +
                    "\n" +
                    "10245\n" +
                    "10000\n" +
                    "13348\n";

    public static final String REVCOMP = ">ONE Homo sapiens alu\n" +
                    "CGGAGTCTCGCTCTGTCGCCCAGGCTGGAGTGCAGTGGCGCGATCTCGGCTCACTGCAAC\n" +
                    "CTCCGCCTCCCGGGTTCAAGCGATTCTCCTGCCTCAGCCTCCCGAGTAGCTGGGATTACA\n" +
                    "GGCGCGCGCCACCACGCCCGGCTAATTTTTGTATTTTTAGTAGAGACGGGGTTTCACCAT\n" +
                    "GTTGGCCAGGCTGGTCTCGAACTCCTGACCTCAGGTGATCCGCCCGCCTCGGCCTCCCAA\n" +
                    "AGTGCTGGGATTACAGGCGTGAGCCACCGCGCCCGGCCTTTTTGAGACGGAGTCTCGCTC\n" +
                    "TGTCGCCCAGGCTGGAGTGCAGTGGCGCGATCTCGGCTCACTGCAACCTCCGCCTCCCGG\n" +
                    "GTTCAAGCGATTCTCCTGCCTCAGCCTCCCGAGTAGCTGGGATTACAGGCGCGCGCCACC\n" +
                    "ACGCCCGGCTAATTTTTGTATTTTTAGTAGAGACGGGGTTTCACCATGTTGGCCAGGCTG\n" +
                    "GTCTCGAACTCCTGACCTCAGGTGATCCGCCCGCCTCGGCCTCCCAAAGTGCTGGGATTA\n" +
                    "CAGGCGTGAGCCACCGCGCCCGGCCTTTTTGAGACGGAGTCTCGCTCTGTCGCCCAGGCT\n" +
                    "GGAGTGCAGTGGCGCGATCTCGGCTCACTGCAACCTCCGCCTCCCGGGTTCAAGCGATTC\n" +
                    "TCCTGCCTCAGCCTCCCGAGTAGCTGGGATTACAGGCGCGCGCCACCACGCCCGGCTAAT\n" +
                    "TTTTGTATTTTTAGTAGAGACGGGGTTTCACCATGTTGGCCAGGCTGGTCTCGAACTCCT\n" +
                    "GACCTCAGGTGATCCGCCCGCCTCGGCCTCCCAAAGTGCTGGGATTACAGGCGTGAGCCA\n" +
                    "CCGCGCCCGGCCTTTTTGAGACGGAGTCTCGCTCTGTCGCCCAGGCTGGAGTGCAGTGGC\n" +
                    "GCGATCTCGGCTCACTGCAACCTCCGCCTCCCGGGTTCAAGCGATTCTCCTGCCTCAGCC\n" +
                    "TCCCGAGTAGCTGGGATTACAGGCGCGCGCCACCACGCCCGGCTAATTTTTGTATTTTTA\n" +
                    "GTAGAGACGGGGTTTCACCATGTTGGCCAGGCTGGTCTCGAACTCCTGACCTCAGGTGAT\n" +
                    "CCGCCCGCCTCGGCCTCCCAAAGTGCTGGGATTACAGGCGTGAGCCACCGCGCCCGGCCT\n" +
                    "TTTTGAGACGGAGTCTCGCTCTGTCGCCCAGGCTGGAGTGCAGTGGCGCGATCTCGGCTC\n" +
                    "ACTGCAACCTCCGCCTCCCGGGTTCAAGCGATTCTCCTGCCTCAGCCTCCCGAGTAGCTG\n" +
                    "GGATTACAGGCGCGCGCCACCACGCCCGGCTAATTTTTGTATTTTTAGTAGAGACGGGGT\n" +
                    "TTCACCATGTTGGCCAGGCTGGTCTCGAACTCCTGACCTCAGGTGATCCGCCCGCCTCGG\n" +
                    "CCTCCCAAAGTGCTGGGATTACAGGCGTGAGCCACCGCGCCCGGCCTTTTTGAGACGGAG\n" +
                    "TCTCGCTCTGTCGCCCAGGCTGGAGTGCAGTGGCGCGATCTCGGCTCACTGCAACCTCCG\n" +
                    "CCTCCCGGGTTCAAGCGATTCTCCTGCCTCAGCCTCCCGAGTAGCTGGGATTACAGGCGC\n" +
                    "GCGCCACCACGCCCGGCTAATTTTTGTATTTTTAGTAGAGACGGGGTTTCACCATGTTGG\n" +
                    "CCAGGCTGGTCTCGAACTCCTGACCTCAGGTGATCCGCCCGCCTCGGCCTCCCAAAGTGC\n" +
                    "TGGGATTACAGGCGTGAGCCACCGCGCCCGGCCTTTTTGAGACGGAGTCTCGCTCTGTCG\n" +
                    "CCCAGGCTGGAGTGCAGTGGCGCGATCTCGGCTCACTGCAACCTCCGCCTCCCGGGTTCA\n" +
                    "AGCGATTCTCCTGCCTCAGCCTCCCGAGTAGCTGGGATTACAGGCGCGCGCCACCACGCC\n" +
                    "CGGCTAATTTTTGTATTTTTAGTAGAGACGGGGTTTCACCATGTTGGCCAGGCTGGTCTC\n" +
                    "GAACTCCTGACCTCAGGTGATCCGCCCGCCTCGGCCTCCCAAAGTGCTGGGATTACAGGC\n" +
                    "GTGAGCCACCGCGCCCGGCC\n" +
                    ">TWO IUB ambiguity codes\n" +
                    "TAGGDHACHATCRGTRGVTGAGWTATGYTGCTGTCABACDWVTRTAAGAVVAGATTTNDA\n" +
                    "GASMTCTGCATBYTTCAAKTTACMTATTACTTCATARGGYACMRTGTTTTYTATACVAAT\n" +
                    "TTCTAKGDACKADACTATATNTANTCGTTCACGBCGYSCBHTANGGTGATCGTAAAGTAA\n" +
                    "CTATBAAAAGATSTGWATBCSGAKHTTABBAACGTSYCATGCAAVATKTSKTASCGGAAT\n" +
                    "WVATTTNTCCTTCTTCTTDDAGTGGTTGGATACVGTTAYMTMTBTACTTTHAGCTAGBAA\n" +
                    "AAGAGKAAGTTRATWATCAGATTMDDTTTAAAVAAATATTKTCYTAAATTVCNKTTRACG\n" +
                    "ADTATATTTATGATSADSCAATAWAGCGRTAGTGTAAGTGACVGRADYGTGCTACHVSDT\n" +
                    "CTVCARCSYTTAATATARAAAATTTAATTTACDAATTGBACAGTAYAABATBTGCAGBVG\n" +
                    "TGATGGDCAAAATBNMSTTABKATTGGSTCCTAGBTTACTTGTTTAGTTTATHCGATSTA\n" +
                    "AAGTCGAKAAASTGTTTTAWAKCAGATATACTTTTMTTTTGBATAGAGGAGCMATGATRA\n" +
                    "AAGGNCAYDCCDDGAAAGTHGBTAATCKYTBTACBGTBCTTTTTGDTAASSWTAAWAARA\n" +
                    "TTGGCTAAGWGRADTYACATAGCTCBTAGATAWAGCAATNGTATMATGTTKMMAGTAWTC\n" +
                    "CCNTSGAAWATWCAAAAMACTGAADNTYGATNAATCCGAYWNCTAACGTTAGAGDTTTTC\n" +
                    "ATCTGGKRTAVGAABVCTGWGBTCTDVGKATTBTCTAAGGVADAAAVWTCTAGGGGAGGG\n" +
                    "TTAGAACAATTAAHTAATNAAATGCATKATCTAAYRTDTCAGSAYTTYHGATRTTWAVTA\n" +
                    "BGNTCDACAGBCCRCAGWCRTCABTGMMAWGMCTCAACCGATRTGBCAVAATCGTDWDAA\n" +
                    "CAYAWAATWCTGGTAHCCCTAAGATAACSCTTAGTGSAACAWTBGTCDTTDGACWDBAAC\n" +
                    "HTTTNGSKTYYAAYGGATNTGATTTAARTTAMBAATCTAAGTBTCATYTAACTTADTGTT\n" +
                    "TCGATACGAAHGGCYATATACCWDTKYATDCSHTDTCAAAATGTGBACTGSCCVGATGTA\n" +
                    "TCMMAGCCTTDAAABAATGAAGAGTAACTHATMGVTTAATAACCCGGTTVSANTGCAATT\n" +
                    "GTGAGATTTAMGTTTAMAAYGCTGACAYAAAAAGGCACAMYTAAGVGGCTGGAABVTACG\n" +
                    "GATTSTYGTBVAKTATWACCGTGTKAGTDTGTATGTTTAAAGGAAAAAGTAACATARAAA\n" +
                    "GGTYCAMNYAAABTATAGNTSATANAGTCATCCTATWADKAACTRGTMSACDGTATSAYT\n" +
                    "AAHSHGTAABYGACTYTATADTGSTATAGAGAAATCGNTAAAGGAAATCAGTTGTNCYMV\n" +
                    "TNACDRTATBNATATASTAGAAMSCGGGANRCKKMCAAACATTNAGTCTRMAATBMTACC\n" +
                    "CGTACTTCTBGDSYAATWGAAAATGACADDCHAKAAAYATATTKTTTTCACANACWAGAA\n" +
                    "AKATCCTTATTAYKHKCTAAACARTATTTTDATBTVWCYGCAATACTAGGKAAASTTDGA\n" +
                    "MGGCHTTHAATVCAHDRYAGGRCTATACGTCMAGAGAGCTBTHGNACARTCCBDCTAAGA\n" +
                    "GCGGCTTTARTAAAGAATCCNAGTAWBTGACTTGAATTACWTVACAGAAABCAATNAAAC\n" +
                    "CGTNTRANTTGAYCMAWBADTANABRGGTKTHTWTAGTTVCTMBKTAGMTVKCCAGCANT\n" +
                    "TVAGSWTTAGCCGCRHTTTCCTTHNTATTAAGAAGAATAGGMTRAARTCTABGTACDTTT\n" +
                    "TATAAVDHAHTATAGATCCTAGTAAGYTWATDWCATGAGGGATAGTAAMDMNGBASTWAM\n" +
                    "TSTATRBAYDABATGTATATYCGCACTGTTTTAACMCWBTATAWAGTATBTSTATVTTAR\n" +
                    "CCTMTTAAKADATCAACTAATYTSVTAKGDATTATGCKTCAYCAKAATACTTKAANGAGT\n" +
                    "ATTSDAGATCGGAAATACTTAAYAAVGTATMCGCTTGTGTDCTAATYTATTTTATTTWAA\n" +
                    "CAGWRCTATGTAGMTGTTTGTTYKTNGTTKTCAGAACNTRACCTACKTGSRATGTGGGGG\n" +
                    "CTGTCATTAAGTAAATNGSTTABCCCCTCGCAGCTCWHTCGCGAAGCAVATGCKACGHCA\n" +
                    "ACAKTTAATAACASAAADATTWNYTGTAATTGTTCGTMHACHTWATGTGCWTTTTGAAHY\n" +
                    "ACTTTGTAYAMSAAACTTAADAAATATAGTABMATATYAATGSGGTAGTTTGTGTBYGGT\n" +
                    "TWSGSVGWMATTDMTCCWWCABTCSVACAGBAATGTTKATBGTCAATAATCTTCTTAAAC\n" +
                    "ARVAATHAGYBWCTRWCABGTWWAATCTAAGTCASTAAAKTAAGVKBAATTBGABACGTA\n" +
                    "AGGTTAAATAAAAACTRMDTWBCTTTTTAATAAAAGATMGCCTACKAKNTBAGYRASTGT\n" +
                    "ASSTCGTHCGAAKTTATTATATTYTTTGTAGAACATGTCAAAACTWTWTHGKTCCYAATA\n" +
                    "AAGTGGAYTMCYTAARCSTAAATWAKTGAATTTRAGTCTSSATACGACWAKAASATDAAA\n" +
                    "TGYYACTSAACAAHAKTSHYARGASTATTATTHAGGYGGASTTTBGAKGATSANAACACD\n" +
                    "TRGSTTRAAAAAAAACAAGARTCVTAGTAAGATAWATGVHAAKATWGAAAAGTYAHVTAC\n" +
                    "TCTGRTGTCAWGATRVAAKTCGCAAVCGASWGGTTRTCSAMCCTAACASGWKKAWDAATG\n" +
                    "ACRCBACTATGTGTCTTCAAAHGSCTATATTTCGTVWAGAAGTAYCKGARAKSGKAGTAN\n" +
                    "TTTCYACATWATGTCTAAAADMDTWCAATSTKDACAMAADADBSAAATAGGCTHAHAGTA\n" +
                    "CGACVGAATTATAAAGAHCCVAYHGHTTTACATSTTTATGNCCMTAGCATATGATAVAAG\n" +
                    ">THREE Homo sapiens frequency\n" +
                    "ATATTTATCTTTTCACTTCCTACATTGGTCAGACCATTATTCGACACGTGGCGTCATTTT\n" +
                    "GTCATACCGGGTAATGTTGGAAACAAAACGTACTGATAAAATACTGAGTTGTAAACTCTA\n" +
                    "ATCAGATAACGCGCTTGGATATTAAGATTCACACAGGGGTTTCGGCTGTAAAAAAACTTG\n" +
                    "TGGAGCTGTTCTGGGACAGATAAGTTGTACCTCGTACTTAGCTAATTAATGAACCAACTG\n" +
                    "ATTACGATAGAACAATTCTGAGGCCGCCAGGACAGCCAAATTTTAATCTTATAAAGCTGG\n" +
                    "AAACAGCCGGTATTAGCTTCTCGCATACTTTGCCTGCATTGGTACCTTACAGATATCAGC\n" +
                    "GTAGTCATATACACCTCGGTCTCAGCTAAGCTTGTATCTCTTAGAGTAGTTCAAAGATAG\n" +
                    "TGGACAATACCTGTGGAATCGATTGCAGATATGGATTTATTTAACTACTGAGTCTCATTC\n" +
                    "ACAAGCTAAGCAAGGAGCACGTTTTGGTGCCGGCATACCGATTTGCTATCATGTCAGCAA\n" +
                    "ATTTGCGTTGTATTCCTAGTTGCACCCATTAAGGCCACACTCCGAACCTAATTATTACAT\n" +
                    "CGCAAAGACATGTACGAAGGACCCGATGTCGAATAGAAGGGAGGACTGTTCATTGGAAGC\n" +
                    "TAGACCAGAGGAATCGCAAAGATGCAACTCTTACAATAAAAATCTAATTTCAGTCAACAC\n" +
                    "GCAATTTCTATAAGGTTTCCGATAATAATGAACCGTCTTCCACAGGGGAATTTGCCATGC\n" +
                    "TCGTAAAAGTAGTTAATCCAAGTAGAAGAAATTTTGATAATGTTTTAAGTTGGCACGAAG\n" +
                    "GAATTCAGAGAGATCTTACCTAACAAAGGCATTAGTAGATGTTCCTTGGTTCACACTCGG\n" +
                    "TCAATCAGAGCACATACTACGGGCGATACCGGGAATGACACAACATCAATGAGATTGTTA\n" +
                    "AGTGAGGTAATTGACTTTAGAGGACTCGATCAGTATACTGTCACTATGAACATCGTATTA\n" +
                    "ATTGTTATCCGATATATACACCACCGATTTGCTTGTGCAAGGTTACAGACCCATTCGATA\n" +
                    "AATACAAACACGGAGCGATATTATTTAAGGAGTGCTGTCTTCAAAAGAATTATTCCCACA\n" +
                    "CCGACATAAGAACTTCGCTCCGTCATTCCAGATTTAAATAACATAACGTAACGCTTTGCT\n" +
                    "GATAACATAACATAACCGAGAATTTGCTTAGGAAATTTGGAGCAATATTGCATTGTTTCT\n" +
                    "CAGTCATCACAAGGCCCGCCAAAGAACTCTGAGAATCAGGATTCAACATGATTGGTAAGA\n" +
                    "CTCTATATATATAACTTAATTCTTGTGTCCGGAGATAGAAAGAGGACGAGAGATACTACG\n" +
                    "AAAGAAAGTGTACTTCGATGTATCAATTCAGACGCCTTCTCTATCATCAACATTATAGGT\n" +
                    "CTCGTATATGCTCGGCGCGATCTGCTTCTCTCCGCCAATAGCCCCATAGTGTATTTCAAG\n" +
                    "CGCAGTAACAGTGAAATCGTTACGAAGGTAGGGATGTTGCTTATAATTGTCGTAACTTAT\n" +
                    "CGCTTATGTATCTTTCAAGAATGAACGGCAGCATATACATACGTTCTACCTTTAGCTACA\n" +
                    "AAGCATCCATATACTCCCTCTCATGATTGAAACTCTTCCCTATTTTGTAGCCAATAGTGA\n" +
                    "AAGCGTATTAGTATAAATTCGTCGGTTTTTCACTCGCAACTGTTATACTCTGCAAACAAA\n" +
                    "CGAAAGCCTCATAGTACAAACCTAAAGCTACATACTTCATCATTGGCAGACCAGTGGCGG\n" +
                    "TATTTCTACGGAAGCATCACTATAGATATAAAGTTTCCCTTCATGTACGTCTGTTAACCA\n" +
                    "TATCACAAGAAACTGCTATCTCTGTCACGTAACAATTCACGCGCCTTATCGCCAAATGTT\n" +
                    "CATATATGCGCGGTATACGTATGAACGAATACTAATTAGTATAACGGAGGATTCACGGGA\n" +
                    "GGGATACTTGGGGCATTTATAAATCGTCTAAAAATTTTCTATCAGCACTTGCGGGTTATA\n" +
                    "GTGGATTACTAGGCAACATAATATTCTGTATTGGTCCAAATGACGCTATAGATAAATTAG\n" +
                    "CAAAATACATTGTTTCCATTTATGTAAGTCGAAACTCCAGGACTCCCGGGAACCAGTTAA\n" +
                    "ACCGTCTGGAAAAGACACATTGTGAGCGGGACTTCAATGATAGCTTTCAATGAGCTTCTC\n" +
                    "ATGCTTGGGGTCTGTACATATATGTTGGCGAAATTATCGTCTGTATTCTGTTATGCTTTG\n" +
                    "ATCATGGGTTATTAGTATAGTGTCCGGTTAAGTACCAATACCGCTAGAGACCCGACCTAA\n" +
                    "GTCGATAACTAACGATCATCGACGTAAGGATCGTCTCGATCAGTACTTCAGTCTAGATCT\n" +
                    "GGGAATAGTAACTCGTTAGTGAACTATGTCGTGTCATAACTCTAAAATGCAATCAAATCT\n" +
                    "TATTATTGAGTATTGATTATATAAAGCATCCGCTTAGCTTTACCCTCAAATGTTATATGC\n" +
                    "AATTTAAAGCGCTTGATATCGTCTACTCAAGTTCAGGTTTCACATGGCCGCAACGTGACG\n" +
                    "TTATTAGAGGTGGGTCATCATCTCTGAGGCTAGTGATGTTGAATACTCATTGAATGGGAA\n" +
                    "GTGGAATACCATGCTCGTAGGTAACAGCATGACCTATAAAATATACTATGGGTGTGTGGT\n" +
                    "AGATCAATATTGTTCAAGCATATCGTAACAATAACGGCTGAAATGTTACTGACATGAAAG\n" +
                    "AGGGAGTCCAAACCATTCTAACAGCTGATCAAGTCGTCTAAAAACGCCTGGTTCAGCCTT\n" +
                    "AAGAGTTATAAGCCAGACAAATTGTATCAATAGAGAATCCGTAAATTCCTCGGCCAACCT\n" +
                    "CTTGCAAAGACATCACTATCAATATACTACCGTGATCTTAATTAGTGAACTTATATAAAT\n" +
                    "ATCTACAACCAGATTCAACGGAAAAGCTTTAGTGGATTAGAAATTGCCAAGAATCACATT\n" +
                    "CATGTGGGTTCGAATGCTTTAGTAATACCATTTCGCCGAGTAGTCACTTCGCTGAACTGT\n" +
                    "CGTAAATTGCTATGACATAATCGAAAAGGATTGTCAAGAGTCGATTACTGCGGACTAATA\n" +
                    "ATCCCCACGGGGGTGGTCTCATGTCTCCCCAGGCGAGTGGGGACGGTTGATAAACACGCT\n" +
                    "GCATCGCGGACTGATGTTCCCAGTATTACATAGTCACATTGGATTGCGAGTAGTCTACCT\n" +
                    "ATTTATGAGCGAGAGATGCCTCTAACTACTTCGACTTTTAAAACCTTTCCACGCCAGTAT\n" +
                    "TCGGCGAAAGGGAAGTATTAAGGGTTGTCATAATTAAGCTGATACCACTTCAGACTTTGC\n" +
                    "TCTACTTCTGTCTTTCATTGGTTTAGTAAAGTCTGTCCATTCGTCGAGACCGTCTTTTGC\n" +
                    "AGCCTCATTCTACCAACTGCTCCGACTCTTAGTCTGCTTCTCCCAGCGTTATAACAAGAG\n" +
                    "GCATTTTGTCATCCTTAAAACAATAATAAAGAACTCGGAGCACTGATATAATGACTGAAT\n" +
                    "TAGAACCGCTTAAAAATACAACGAATAGATAAGACTATCGGATAAGATCTAATATGTAGT\n" +
                    "GATTAAGCCCTTTATTAATTAATAATAGTTACCCTTTCTGATGTAACGCGACATATTACG\n" +
                    "ATTTAGTGGCACGTCTGAATTGCAAAGCAGATCTCTACCCGATTTTTATTATAAATCCCG\n" +
                    "TATACATCTTGACTTGAGTAATTGTTCATCTTTTTATATCTCTTCGTACTACAAATAATT\n" +
                    "AATATCTCAACCCGTATTGTGTGATTCTAATTACCAACAGAATACGAGGAGGTTTTTGCT\n" +
                    "TAGGGCCATATATAATGAATCTATCTCGTTTATTCGCGGAACCCGAGATAACATTACGAT\n" +
                    "GTAACTATTTTAGAGAACTTAATACAAGAAACATTGCTGATTACTCATAACTAAATGCTT\n" +
                    "GGTAATATATCCTCAGTGCCCCTACCATCTTTTACGCAGGGATGTAATTACTTAGGATTC\n" +
                    "ATTGTGTAAGAATTACAATGAACGATGGATATGAAGGCATGTTGCGAGGTGTTCCTTGGT\n" +
                    "ATGTGAAGTTCGCAGGGCAACAAAAATTTCGCAGAATAGGCCTCAAAGTATTGGTAAAGA\n" +
                    "AGACAACTAATCATCACGAGCTTCTGATATCAATACGAACGAGTCCTGTGATGGATGAAA\n" +
                    "GAAAGTCGTATCGAAAATGTCAAGAGTCTGCCCAATGTAACTTACTTCAAAAAATAACGC\n" +
                    "TTCCGCCAAGTACGTTCGAATAAACGTAATTTTAAAAATACATAAGGGGTGTTAGAAAGT\n" +
                    "AAGCGACGGGATATAAGTTAGACTCAAGATTCCGCCGTAAAACGAGACTGATTCCGAAGA\n" +
                    "TTGTTCGTGGATCTGGTCATGACTTTCACTGAGTAAGGAGTTTCGACATATGTCAATAAA\n" +
                    "CACAAAAATAGAAGCTATTCGATCTGAAAAATATTAGGACAAGAAACTATCTCACGCTAG\n" +
                    "CCCAGAATATTCACTCACCCACGGGCGATACTAAAGCACTATATAGTCGCGTGATTACTA\n" +
                    "TACATATGGTACACATAAGAATCACGATCAGGTTCTCAATTTTCAACAATATATGTTTAT\n" +
                    "TTGCATAGGTAATATTAGGCCTTTAAGAGAAGGATGGGTGAGATACTCCGGGGATGGCGG\n" +
                    "CAATAAAGAAAAACACGATATGAGTAATAGGATCCTAATATCTTGGCGAGAGACTTAAGG\n" +
                    "TACGAATTTTGCGCAATCTATTTTTTACTTGGCCAGAATTCATGTATGGTATAAGTACGA\n" +
                    "ACTTTTTTGATCACTTTCATGGCTACCTGATTAGGATAGTTTGAGGAATTTCCCAAATAT\n" +
                    "ACCGATTTAATATACACTAGGGCTTGTCACTTTGAGTCAGAAAAAGAATATAATTACTTA\n" +
                    "GGGTAATGCTGCATACATATTCTTATATTGCAAAGGTTCTCTGGGTAATCTTGAGCCTTC\n" +
                    "ACGATACCTGGTGAAGTGTT\n";

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

    @Test
    public void fastaredux_gcc5() throws Exception {
        TestRunner.run("fastaredux.gcc-5", new String[]{"1000"}, "", FASTAREDUX, "", 0);
    }

    @Test
    public void fastaredux_gpp() throws Exception {
        TestRunner.run("fastaredux.gpp", new String[]{"1000"}, "", FASTAREDUX, "", 0);
    }

    @Test
    public void fastaredux_gpp2() throws Exception {
        TestRunner.run("fastaredux.gpp-2", new String[]{"1000"}, "", FASTAREDUX, "", 0);
    }

    @Test
    public void fastaredux_gpp5() throws Exception {
        TestRunner.run("fastaredux.gpp-5", new String[]{"1000"}, "", FASTAREDUX, "", 0);
    }

    @Test
    public void fannkuchredux_cint() throws Exception {
        TestRunner.run("fannkuchredux.cint", new String[]{"7"}, "", FANNKUCH, "", 0);
    }

    @Test
    public void fannkuchredux_gcc() throws Exception {
        TestRunner.run("fannkuchredux.gcc", new String[]{"7"}, "", FANNKUCH, "", 0);
    }

    @Test
    public void fannkuchredux_gcc3() throws Exception {
        TestRunner.run("fannkuchredux.gcc-3", new String[]{"7"}, "", FANNKUCH, "", 0);
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

    @Test
    public void mandelbrot_gpp() throws Exception {
        TestRunner.runBinary("mandelbrot.gpp", new String[]{"200"}, "", MANDELBROT_GPP, "", 0);
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

    @Test
    public void mandelbrot_gpp8() throws Exception {
        TestRunner.runBinary("mandelbrot.gpp-8", new String[]{"200"}, "", MANDELBROT, "", 0);
    }

    @Test
    public void mandelbrot_gpp9() throws Exception {
        TestRunner.runBinary("mandelbrot.gpp-9", new String[]{"200"}, "", MANDELBROT, "", 0);
    }

    @Test
    public void binarytrees_gcc() throws Exception {
        TestRunner.run("binarytrees.gcc", new String[]{"10"}, "", BINARYTREES, "", 0);
    }

    @Test
    public void binarytrees_gcc2() throws Exception {
        TestRunner.run("binarytrees.gcc-2", new String[]{"10"}, "", BINARYTREES, "", 0);
    }

    @Test
    public void spectralnorm_cint() throws Exception {
        TestRunner.run("spectralnorm.cint", new String[]{"100"}, "", SPECTRALNORM, "", 0);
    }

    @Test
    public void spectralnorm_gcc() throws Exception {
        TestRunner.run("spectralnorm.gcc", new String[]{"100"}, "", SPECTRALNORM, "", 0);
    }

    @Test
    public void spectralnorm_gcc2() throws Exception {
        TestRunner.run("spectralnorm.gcc-2", new String[]{"100"}, "", SPECTRALNORM, "", 0);
    }

    @Test
    public void spectralnorm_gcc3() throws Exception {
        TestRunner.run("spectralnorm.gcc-3", new String[]{"100"}, "", SPECTRALNORM, "", 0);
    }

    @Test
    public void spectralnorm_gcc5() throws Exception {
        TestRunner.run("spectralnorm.gcc-5", new String[]{"100"}, "", SPECTRALNORM, "", 0);
    }

    @Test
    public void pidigits_cint4() throws Exception {
        assumeTrue(HostTest.isX86);
        TestRunner.run("pidigits.cint-4", new String[]{"850"}, "", PIDIGITS, "", 0);
    }

    @Test
    public void pidigits_gcc() throws Exception {
        assumeTrue(HostTest.isX86);
        TestRunner.run("pidigits.gcc", new String[]{"850"}, "", PIDIGITS, "", 0);
    }

    @Test
    public void pidigits_gcc4() throws Exception {
        assumeTrue(HostTest.isX86);
        TestRunner.run("pidigits.gcc-4", new String[]{"850"}, "", PIDIGITS, "", 0);
    }

    @Test
    public void nbody_cint() throws Exception {
        TestRunner.run("nbody.cint", new String[]{"80000"}, "", NBODY, "", 0);
    }

    @Test
    public void nbody_gcc() throws Exception {
        TestRunner.run("nbody.gcc", new String[]{"80000"}, "", NBODY, "", 0);
    }

    @Test
    public void nbody_gcc2() throws Exception {
        TestRunner.run("nbody.gcc-2", new String[]{"80000"}, "", NBODY, "", 0);
    }

    @Test
    public void nbody_gcc3() throws Exception {
        TestRunner.run("nbody.gcc-3", new String[]{"80000"}, "", NBODY, "", 0);
    }

    @Test
    public void nbody_gcc4() throws Exception {
        TestRunner.run("nbody.gcc-4", new String[]{"80000"}, "", NBODY, "", 0);
    }

    @Test
    public void nbody_gcc6() throws Exception {
        TestRunner.run("nbody.gcc-6", new String[]{"80000"}, "", NBODY, "", 0);
    }

    @Test
    public void nbody_gpp() throws Exception {
        TestRunner.run("nbody.gpp", new String[]{"80000"}, "", NBODY, "", 0);
    }

    @Test
    public void nbody_gpp2() throws Exception {
        TestRunner.run("nbody.gpp-2", new String[]{"80000"}, "", NBODY, "", 0);
    }

    @Test
    public void nbody_gpp3() throws Exception {
        TestRunner.run("nbody.gpp-3", new String[]{"80000"}, "", NBODY, "", 0);
    }

    @Test
    public void nbody_gpp5() throws Exception {
        TestRunner.run("nbody.gpp-5", new String[]{"80000"}, "", NBODY, "", 0);
    }

    @Test
    public void nbody_gpp6() throws Exception {
        TestRunner.run("nbody.gpp-6", new String[]{"80000"}, "", NBODY, "", 0);
    }

    @Test
    public void nbody_gpp7() throws Exception {
        TestRunner.run("nbody.gpp-7", new String[]{"80000"}, "", NBODY, "", 0);
    }

    @Test
    public void nbody_gpp8() throws Exception {
        TestRunner.run("nbody.gpp-8", new String[]{"80000"}, "", NBODY, "", 0);
    }

    @Test
    public void meteor_cint() throws Exception {
        TestRunner.run("meteor.cint", new String[0], "", METEOR, "", 0);
    }

    @Test
    public void meteor_gcc() throws Exception {
        TestRunner.run("meteor.gcc", new String[0], "", METEOR, "", 0);
    }

    @Test
    public void meteor_gpp() throws Exception {
        TestRunner.run("meteor.gpp", new String[0], "", METEOR, "", 0);
    }

    @Test
    public void meteor_gpp2() throws Exception {
        TestRunner.run("meteor.gpp-2", new String[0], "", METEOR, "", 0);
    }

    @Test
    public void meteor_gpp3() throws Exception {
        TestRunner.run("meteor.gpp-3", new String[0], "", METEOR, "", 0);
    }

    @Test
    public void meteor_gpp4() throws Exception {
        TestRunner.run("meteor.gpp-4", new String[0], "", METEOR, "", 0);
    }

    @Test
    public void meteor_gpp5() throws Exception {
        TestRunner.run("meteor.gpp-5", new String[0], "", METEOR, "", 0);
    }

    @Test
    public void meteor_gpp6() throws Exception {
        TestRunner.run("meteor.gpp-6", new String[0], "", METEOR, "", 0);
    }

    @Test
    public void knucleotide_cint() throws Exception {
        TestRunner.run("knucleotide.cint", new String[0], FASTA, KNUCLEOTIDE, "", 0);
    }

    @Test
    public void knucleotide_gcc8() throws Exception {
        TestRunner.run("knucleotide.gcc-8", new String[0], FASTA, KNUCLEOTIDE, "", 0);
    }

    @Test
    public void regexdna_cint2() throws Exception {
        assumeTrue(HostTest.isX86);
        TestRunner.run("regexdna.cint-2", new String[0], FASTA, REGEXDNA, "", 0);
    }

    @Test
    public void regexdna_gcc2() throws Exception {
        assumeTrue(HostTest.isX86);
        TestRunner.run("regexdna.gcc-2", new String[0], FASTA, REGEXDNA, "", 0);
    }

    @Test
    public void revcomp_cint2() throws Exception {
        TestRunner.run("revcomp.cint-2", new String[0], FASTA, REVCOMP, "", 0);
    }

    @Test
    public void revcomp_gcc4() throws Exception {
        TestRunner.run("revcomp.gcc-4", new String[0], FASTA, REVCOMP, "", 0);
    }

    @Test
    public void revcomp_gpp3() throws Exception {
        TestRunner.run("revcomp.gpp-3", new String[0], FASTA, REVCOMP, "", 0);
    }

    @Test
    public void revcomp_gpp5() throws Exception {
        TestRunner.run("revcomp.gpp-5", new String[0], FASTA, REVCOMP, "", 0);
    }
}
