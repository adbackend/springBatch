package com.ex.samplebatch.batch;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.batch.item.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

public class ExcelRowReader implements ItemStreamReader<Row> {

    private final String filePath; // 엑셀 파일 경로

    private FileInputStream fileInputStream; // 파일열기

    private Workbook workbook;

    private Iterator<Row> rowCursor;

    private int currentRowNumber;

    private final String CURRENT_ROW_KEY = "current.row.number";

    public ExcelRowReader(String filePath) throws IOException {
        this.filePath = filePath;
        this.currentRowNumber = 0; // 시트시작 0 초기화
    }

    @Override
    public Row read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if(rowCursor != null && rowCursor.hasNext()){
            currentRowNumber++;
            return rowCursor.next();
        }else{

            return null;
        }
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {

        ItemStreamReader.super.open(executionContext);

        try{

            fileInputStream = new FileInputStream(filePath);
            workbook = WorkbookFactory.create(fileInputStream);

            Sheet sheet = workbook.getSheetAt(0);
            this.rowCursor = sheet.iterator(); // 시트 순회

            // 동일 배치 파라미터에 대해 특정 키 값 "current.row.number" 의 값이 존재한다면 초기화
            if(executionContext.containsKey(CURRENT_ROW_KEY)){
                currentRowNumber = executionContext.getInt(CURRENT_ROW_KEY);
            }

            // 위의 값을 가져와 이미 실행한 부분은 건너 뜀
            for(int i=0; i< currentRowNumber && rowCursor.hasNext(); i++){
                rowCursor.next(); // 이전 모든 행을 건너뛴, 현재 처리해야 할 행으로 이동
            }

        }catch (IOException e){
            throw new ItemStreamException(e);
        }
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        executionContext.putInt(CURRENT_ROW_KEY, currentRowNumber);
    }

    @Override
    public void close() throws ItemStreamException {
        try {
            if(workbook != null){
                workbook.close();
            }
            if(fileInputStream != null){
                fileInputStream.close();
            }

        }catch (IOException e){
            throw new ItemStreamException(e);
        }
    }
}
