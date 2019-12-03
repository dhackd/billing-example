package billing;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

/*
 * 본 내용은 주택 전기요금 기본구조를 기반으로 계산되었습니다. 저압방식이며 고압방식은 고려되지 않았습니다.
 * 
 * 총 6단계 누진제 적용이 되어 있습니다. 현재 계산식에서 7 ~ 8월은 제외됨. (특수일로 지정)
 * 
 */


public class Main {

  /**
   * main method.
   * 
   * @param args
   *          args
   */
  public static void main(String[] args) {

    // ex) 250kWh 사용시 한달 부과요금
    // 250kWh 사용
    // 해당 부분은 kWh로 표현된 것이며 사용량에 따라 가격이 달라집니다.
    getBillingAmount(230);

  }

  public static float getBillingAmount(float electric) {
    // 결과
    double result = 0.0;
    // 사용량
    // float useElectric = 0;
    // 기본요금
    float basicCharge = 0;
    // 전력량 요금
    float amtQtyPowerCharge = 0;
    // 세금
    float addValueTax = 0.1f;
    // 전력산업기반기금
    float fundTax = 0.037f;
    // totalAmount;
    float price = 0.0f;


    // arrayList index
    int index = 0;
    // qtyPower position
    int num = 0;
    // initial step
    @SuppressWarnings("unused")
    int step = 1;
    // qtyUserPowerRefactoringBuket
    ArrayList<Float> tmpQtyPower = new ArrayList<>();

    // kWh당 기본책정요금
    final ArrayList<Integer> usingCharge =
        new ArrayList<>(Arrays.asList(910, 910, 1600, 1600, 7300, 7300));
    final ArrayList<Integer> qtyPower1 = new ArrayList<>(Arrays.asList(100, 200, 300, 400, 500));
    final ArrayList<Float> qtyCharge =
        new ArrayList<>(Arrays.asList(93.3f, 93.3f, 93.3f, 187.9f, 187.9f, 280.6f));
    // 기본요금 설정
    for (Integer power : qtyPower1) {
      if (power >= electric) {
        basicCharge = usingCharge.get(index);
        break;
      }
      index++;
    }

    System.out.println("사용량 = " + electric + "kWh");
    System.out.println("기본요금 = " + toNumFormat(basicCharge) + "원");

    // 전력요금 포지션 찾기
    for (Integer power : qtyPower1) {
      if (power <= electric) {
        num++;
        continue;
      }
      num++;
      break;
    }

    System.out.println("전력요금 = " + qtyCharge.get(num - 1) + "원");

    // 전력요금 계산
    // 금액 계산
    // 200kWh 이하 사용시 월4,000원 한도 감액
    if (electric <= 200) {
      float tmpTotalCharge = basicCharge + (electric * qtyCharge.get(0)) - 4000;
      if (tmpTotalCharge < 0) {
        tmpTotalCharge = 1000;
      }
      System.out.println("전기요금합계 = " + toNumFormat(tmpTotalCharge) + "원");

      // 세금계산
      // 부가가치세 : 10% , 전력산업기반기금 : 3.7%
      float fundTaxCharge = tmpTotalCharge * fundTax;
      System.out.println("전력산업기반기금(10원미만 절사):" + (Math.floor(fundTaxCharge / 10) * 10) + "원");
      float addedTaxCharge = tmpTotalCharge * addValueTax;
      System.out.println("부가가치세(원미만 4사5입):" + toNumFormat(addedTaxCharge) + "원");

      // 총 부과 요금
      price = (float) (tmpTotalCharge + addedTaxCharge + (Math.floor(fundTaxCharge / 10) * 10));
      // result = Math.floor(price / 10) * 10;
      System.out.println("청구금액 = " + toNumFormat(Math.floor(price / 10) * 10) + "원");
    } else {
      // 전체 사용량
      float tmpUseElectric = electric;
      for (int i = 0; i < num; i++) {
        tmpUseElectric = tmpUseElectric - 100;
        // 100단위로 buket에 저장
        float tmpUseElectricCalc = 100;
        // 사용량이 마이너스일 경우 나머지 계산하기 위해 체크
        if (tmpUseElectric < 0) {
          // 나머지 잔량 계산
          // 전체 사용량 - 반복횟수(전력요금 포지션에서 찾은 인덱스) * 사용단위 맞추기(백단위)
          tmpUseElectric = electric - i * 100;
          tmpQtyPower.add(tmpUseElectric);
        } else {
          tmpQtyPower.add(tmpUseElectricCalc);
        }
      }

      float tmpQtyPowerCharge = 0.0f;
      int i = 0;
      for (Float power : tmpQtyPower) {
        i++;
        tmpQtyPowerCharge += power * qtyCharge.get(i);
      }
      System.out.println("사용량요금 합계 = " + toNumFormat(tmpQtyPowerCharge) + "원");

      // 전기요금 합계
      amtQtyPowerCharge = basicCharge + tmpQtyPowerCharge;
      System.out.println("전기요금 합계 = " + toNumFormat(amtQtyPowerCharge) + "원");

      // 세금계산
      // 부가가치세 : 10% , 전력산업기반기금 : 3.7%
      float fundTaxCharge = amtQtyPowerCharge * fundTax;
      System.out.println("전력산업기반기금(10원미만 절사):" + (Math.floor(fundTaxCharge / 10) * 10) + "원");
      float addedTaxCharge = amtQtyPowerCharge * addValueTax;
      System.out.println("부가가치세(원미만 4사5입):" + toNumFormat(addedTaxCharge) + "원");

      // 총 부과 요금
      price = (float) (amtQtyPowerCharge + addedTaxCharge + (Math.floor(fundTaxCharge / 10) * 10));
      // result = Math.floor(price / 10) * 10;
      System.out.println("청구금액 = " + toNumFormat(Math.floor(price / 10) * 10) + "원");
    }
    return price;
  }


  public static String toNumFormat(double result) {
    DecimalFormat dt = new DecimalFormat("#,###");
    return dt.format(result);
  }
}
