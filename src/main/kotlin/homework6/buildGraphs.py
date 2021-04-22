import numpy as np
import json
import matplotlib.pyplot as plt
from matplotlib.backends.backend_pdf import PdfPages

with open("/Users/vol0ncar/spbu_cotlin_hw/results.json", "r") as json_file, PdfPages("Performance_graphs.pdf") as pdf:
    data = json.load(json_file)
    threadsNums = data['threadsNums']
    graphCount = len(data['tests'])

    #Title page
    firstPage = plt.figure(figsize=(6, 4))
    txt = """
    Тестирование времени сортировки слиянием при использовании многопоточности. 
    Mac OS, Catalina, Intel 2,6 GHz 6-Core Intel Core i7, 16 GB RAM.
    """
    firstPage.text(0.015,0.5,txt, transform=firstPage.transFigure, size=10, wrap = True)
    pdf.savefig()
    plt.close()

    for i, test in enumerate(data['tests']):
        cur = i % 3
        #create new figure for new page with three subplots
        if cur == 0:
            fig, axs = plt.subplots(3, figsize=(6, 8))
        exp = int(np.log10(test['arraySize']))
        coeff = test['arraySize']/10**exp
        axs[cur].bar([str(i) for i in threadsNums], test["timeArray"])
        axs[cur].set_title(
            "Array size: ${}10^{}$".format(str(coeff) + "*" if coeff != 1 else "", exp),
            loc='right',
            fontsize=10)
        axs[cur].set_xlabel("Number of threads")
        axs[cur].set_ylabel("Time, seconds")

        # (i+1)%3==0 => 3 grahps were created, save fig to pdf and end f
        if (i + 1) % 3 == 0 or i == graphCount - 1:
            #delete empty subplots in case graphCount % 3 != 0
            if i == graphCount - 1:
                for ax in axs[cur+1:]:
                    fig.delaxes(ax)
            plt.tight_layout()
            pdf.savefig()
            plt.close()
