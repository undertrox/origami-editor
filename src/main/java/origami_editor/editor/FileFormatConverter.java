package origami_editor.editor;

import origami_editor.record.Memo;

public class FileFormatConverter {

    static Memo orihime2svg(Memo mem_tenkaizu, Memo mem_oriagarizu) {
        System.out.println("svg画像出力");
        Memo MemR = new Memo();

        MemR.reset();

        MemR.addLine("<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">");

        MemR.addMemo(mem_tenkaizu);
        MemR.addMemo(mem_oriagarizu);

        MemR.addLine("</svg>");
        return MemR;
    }
}
