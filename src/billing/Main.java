package billing;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

/*
 * 본 내용은 주택 전기요금 기본구조를 기반으로 계산되었습니다.
 * 저압방식이며 고압방식은 고려되지 않았습니다.
 * 
 * 총 6단계 누진제 적용이 되어 있습니다.
 * 현재 계산식에서 7 ~ 8월은 제외됨. (특수일로 지정)
 * 
 */


public class Main {

  public static void main(String[] args) {

    // 결과
    float result = 0.0f;
    // 사용량
    float useElectric = 0.0f;
    // 기본요금
    int basicCharge = 0;
    // 전력량 요금
    int amtQtyPowerCharge = 0;
    // 세금
    float addValueTax = 0.1f;
    // 전력산업기반기금
    float fundTax = 0.037f;

    // arrayList index
    int index = 0;
    // qtyPower position
    int num = 0;
    // initial step
    int step = 1;


    // kWh당 기본책정요금
    final ArrayList<Integer> usingCharge =
        new ArrayList<>(Arrays.asList(910, 910, 1600, 1600, 7300, 7300));
    final ArrayList<Integer> qtyPower = new ArrayList<>(Arrays.asList(50, 101, 201, 301, 401, 501));
    final ArrayList<Float> qtyCharge =
        new ArrayList<>(Arrays.asList(93.3f, 93.3f, 93.3f, 187.9f, 187.9f, 280.6f));

    // ex) 250kWh 사용시 한달 부과요금

    // 250kWh 사용
    // 해당 부분은 kWh로 표현된 것이며 사용량에 따라 가격이 달라집니다.
    useElectric = 100f;

    // 기본요금 설정
    for (Integer qty_power : qtyPower) {
      if (qty_power >= useElectric) {
        basicCharge = usingCharge.get(index);
        break;
      }
      index++;
    }

    System.out.println("사용량 = " + useElectric + "kWh");
    System.out.println("기본요금 = " + toNumFormat(basicCharge) + "원");

    // 전력요금 포지션 찾기
    for (Integer qty_power : qtyPower) {
      if (qty_power <= useElectric) {
        num++;
        continue;
      }
      num++;
      break;
    }
    System.out.println("전력요금 포지션 = " + num);
    System.out.println("전력요금 = " + qtyCharge.get(num - 1) + "원");

    // 전력요금 계산
    // 100kWh당 금액 계산
    if (step == 1) {
      amtQtyPowerCharge += qtyPower.get(0) * qtyCharge.get(0);
    }

    if (num == 0) {
      amtQtyPowerCharge += qtyPower.get(0) * qtyCharge.get(0);
    } else {
      for (int i = 1; i < num; i++) {
        float tmpUsingPower = 0.0f;
        if ((i == 1) || (i == 2)) {
          tmpUsingPower += qtyPower.get(1);
          amtQtyPowerCharge += tmpUsingPower * qtyCharge.get(i);
        } else {
          if ((i == 3) || (i == 4)) {
            tmpUsingPower += qtyPower.get(3);
            amtQtyPowerCharge += tmpUsingPower * qtyCharge.get(i);
          } else {
            tmpUsingPower += qtyPower.get(4);
            amtQtyPowerCharge += tmpUsingPower * qtyCharge.get(i);
          }
        }
      }
    }
    // 전력요금 합산 총금액
    System.out.println("전력요금 합산 = " + toNumFormat(amtQtyPowerCharge) + "원");
    // 전력요금 총 합산금액 + 기본요금
    System.out.println("기본요금 + 전력요금 합산 = " + toNumFormat((basicCharge + amtQtyPowerCharge)) + "원");
    int tmpTotalCharge = basicCharge + amtQtyPowerCharge;

    // 세금계산
    // 부가가치세 : 10% , 전력산업기반기금 : 3.7%
    int addedTaxCharge = (int) (tmpTotalCharge * addValueTax);
    int fundTaxCharge = (int) (tmpTotalCharge * fundTax);

    // 총 부과 요금
    result = tmpTotalCharge + addedTaxCharge + fundTaxCharge;
    System.out.println("전력사용금액 = " + toNumFormat((int) result) + "원");
  }

  public static String toNumFormat(int num) {
    DecimalFormat dt = new DecimalFormat("#,###");
    return dt.format(num);
  }
}
